package com.optimize.kopesa.afis.service.service;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimize.common.blob.BlobStorageService;
import com.optimize.common.blob.kafka.FingerMatchStatus;
import com.optimize.common.blob.kafka.FingerWorkerRequest;
import com.optimize.common.blob.kafka.FingerWorkerResponse;
import com.optimize.common.blob.kafka.PipelineTimings;
import com.optimize.kopesa.afis.service.config.AfisPipelineProperties;
import com.optimize.kopesa.afis.service.service.ai.FingerprintVectorizer;
import com.optimize.kopesa.afis.service.service.metrics.AfisMetricsService;
import com.optimize.kopesa.afis.service.service.qdrant.QdrantIndexService;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points.ScoredPoint;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FingerMatchingPipelineService {

    private static final Logger log = LoggerFactory.getLogger(FingerMatchingPipelineService.class);

    private final BlobStorageService blobStorageService;
    private final FingerprintVectorizer fingerprintVectorizer;
    private final QdrantIndexService qdrantIndexService;
    private final AfisPipelineProperties properties;
    private final AfisMetricsService metricsService;

    public FingerMatchingPipelineService(
        BlobStorageService blobStorageService,
        FingerprintVectorizer fingerprintVectorizer,
        QdrantIndexService qdrantIndexService,
        AfisPipelineProperties properties,
        AfisMetricsService metricsService
    ) {
        this.blobStorageService = blobStorageService;
        this.fingerprintVectorizer = fingerprintVectorizer;
        this.qdrantIndexService = qdrantIndexService;
        this.properties = properties;
        this.metricsService = metricsService;
    }

    public FingerWorkerResponse process(FingerWorkerRequest request) {
        long pipelineStart = System.currentTimeMillis();
        PipelineTimings timings = new PipelineTimings();
        FingerWorkerResponse response = new FingerWorkerResponse();
        response.setRid(request.getRid());
        response.setFingerId(request.getFingerId());

        try {
            long s3Start = System.currentTimeMillis();
            byte[] imageBytes = blobStorageService.downloadUri(request.getImageUri());
            timings.setS3FetchMs(System.currentTimeMillis() - s3Start);
            metricsService.recordS3Fetch(timings.getS3FetchMs());

            long vectorStart = System.currentTimeMillis();
            float[] embedding = fingerprintVectorizer.vectorize(imageBytes);
            timings.setVectorizeMs(System.currentTimeMillis() - vectorStart);
            metricsService.recordVectorize(timings.getVectorizeMs());

            long hnswStart = System.currentTimeMillis();
            List<ScoredPoint> candidates = qdrantIndexService.search(embedding, properties.getHnswTopK());
            timings.setHnswMs(System.currentTimeMillis() - hnswStart);
            metricsService.recordHnsw(timings.getHnswMs());

            FingerprintTemplate probeTemplate = buildTemplate(imageBytes);
            imageBytes = null;

            long sourceStart = System.currentTimeMillis();
            FingerprintMatcher matcher = new FingerprintMatcher(probeTemplate);
            double threshold = request.getThreshold() > 0 ? request.getThreshold() : properties.getMatchThreshold();
            String matchedRid = null;
            double highestScore = 0d;

            for (ScoredPoint candidate : candidates) {
                String candidateRid = payloadString(candidate, "rid");
                if (request.getRid().equals(candidateRid)) {
                    continue;
                }
                String candidateUri = payloadString(candidate, "image_uri");
                if (candidateUri == null) {
                    continue;
                }
                byte[] candidateImage = blobStorageService.downloadUri(candidateUri);
                FingerprintTemplate candidateTemplate = buildTemplate(candidateImage);
                candidateImage = null;
                double score = matcher.match(candidateTemplate);
                highestScore = Math.max(highestScore, score);
                if (score >= threshold) {
                    matchedRid = candidateRid;
                    break;
                }
            }
            timings.setSourceafisMs(System.currentTimeMillis() - sourceStart);
            metricsService.recordSourceAfis(timings.getSourceafisMs());

            response.setHighestScore(highestScore);
            response.setTimings(timings);
            response.setFingerprintTemplateBase64(Base64.getEncoder().encodeToString(probeTemplate.toByteArray()));

            if (matchedRid != null) {
                response.setStatus(FingerMatchStatus.DUPLICATE);
                response.setMatchedRid(matchedRid);
            } else {
                String qdrantPointId = qdrantIndexService.upsert(
                    request.getRid(),
                    request.getFingerId(),
                    embedding,
                    request.getImageUri()
                );
                response.setStatus(FingerMatchStatus.UNIQUE);
                response.setQdrantPointId(qdrantPointId);
            }
        } catch (Exception e) {
            log.error("Finger pipeline failed for rid={} finger={}: {}", request.getRid(), request.getFingerId(), e.getMessage(), e);
            response.setStatus(FingerMatchStatus.ERROR);
            response.setErrorMessage(e.getMessage());
        }

        metricsService.recordPipelineTotal(System.currentTimeMillis() - pipelineStart);
        return response;
    }

    private FingerprintTemplate buildTemplate(byte[] imageBytes) {
        return new FingerprintTemplate(
            new FingerprintImage(imageBytes, new FingerprintImageOptions().dpi(500))
        );
    }

    private String payloadString(ScoredPoint point, String key) {
        if (!point.getPayloadMap().containsKey(key)) {
            return null;
        }
        JsonWithInt.Value value = point.getPayloadMap().get(key);
        if (value.hasStringValue()) {
            return value.getStringValue();
        }
        return null;
    }
}
