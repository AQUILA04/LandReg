package com.optimize.kopesa.afis.service.service.ai;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.optimize.kopesa.afis.service.config.AfisPipelineProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class FingerprintVectorizer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(FingerprintVectorizer.class);

    private final AfisPipelineProperties properties;
    private final ResourceLoader resourceLoader;
    private OrtEnvironment ortEnvironment;
    private OrtSession ortSession;
    private boolean onnxEnabled;

    public FingerprintVectorizer(AfisPipelineProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    void init() {
        try {
            Resource resource = resourceLoader.getResource(properties.getOnnx().getModelPath());
            if (resource.exists()) {
                ortEnvironment = OrtEnvironment.getEnvironment();
                try (InputStream modelStream = resource.getInputStream()) {
                    ortSession = ortEnvironment.createSession(modelStream.readAllBytes(), new OrtSession.SessionOptions());
                }
                onnxEnabled = true;
                log.info("ONNX fingerprint model loaded from {}", properties.getOnnx().getModelPath());
            } else {
                log.warn("ONNX model not found at {}, using deterministic fallback embedding", properties.getOnnx().getModelPath());
            }
        } catch (Exception e) {
            log.warn("ONNX initialization failed, using fallback embedding: {}", e.getMessage());
        }
    }

    public float[] vectorize(byte[] imageBytes) {
        if (onnxEnabled) {
            return vectorizeWithOnnx(imageBytes);
        }
        return fallbackVector(imageBytes);
    }

    private float[] vectorizeWithOnnx(byte[] imageBytes) {
        int vectorSize = properties.getQdrant().getVectorSize();
        long[] shape = new long[] { 1, imageBytes.length };
        try (OnnxTensor rawImageTensor = OnnxTensor.createTensor(ortEnvironment, FloatBuffer.wrap(toNormalizedFloats(imageBytes)), shape);
            OnnxTensor outputEmbeddingTensor = (OnnxTensor) ortSession.run(
                Collections.singletonMap(ortSession.getInputNames().iterator().next(), rawImageTensor)
            ).get(0)) {
            float[][] output = (float[][]) outputEmbeddingTensor.getValue();
            return output[0];
        } catch (OrtException e) {
            throw new IllegalStateException("ONNX vectorization failed", e);
        }
    }

    private float[] toNormalizedFloats(byte[] imageBytes) {
        float[] floats = new float[imageBytes.length];
        for (int i = 0; i < imageBytes.length; i++) {
            floats[i] = (imageBytes[i] & 0xFF) / 255.0f;
        }
        return floats;
    }

    private float[] fallbackVector(byte[] imageBytes) {
        int vectorSize = properties.getQdrant().getVectorSize();
        float[] vector = new float[vectorSize];
        if (imageBytes == null || imageBytes.length == 0) {
            return vector;
        }
        for (int i = 0; i < imageBytes.length; i++) {
            vector[i % vectorSize] += (imageBytes[i] & 0xFF) / 255.0f;
        }
        float norm = 0f;
        for (float value : vector) {
            norm += value * value;
        }
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
        return vector;
    }

    @PreDestroy
    @Override
    public void close() {
        if (ortSession != null) {
            try {
                ortSession.close();
            } catch (OrtException e) {
                log.warn("Failed to close ONNX session: {}", e.getMessage());
            }
        }
    }
}
