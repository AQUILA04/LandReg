package com.optimize.kopesa.afis.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "afis.pipeline")
public class AfisPipelineProperties {

    private String version = "v2";
    private final Storage storage = new Storage();
    private final Qdrant qdrant = new Qdrant();
    private final Onnx onnx = new Onnx();
    private double matchThreshold = 65d;
    private int hnswTopK = 100;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Storage getStorage() {
        return storage;
    }

    public Qdrant getQdrant() {
        return qdrant;
    }

    public Onnx getOnnx() {
        return onnx;
    }

    public double getMatchThreshold() {
        return matchThreshold;
    }

    public void setMatchThreshold(double matchThreshold) {
        this.matchThreshold = matchThreshold;
    }

    public int getHnswTopK() {
        return hnswTopK;
    }

    public void setHnswTopK(int hnswTopK) {
        this.hnswTopK = hnswTopK;
    }

    public static class Storage {
        private boolean enabled = true;
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static class Qdrant {
        private String host = "localhost";
        private int grpcPort = 6334;
        private String collection = "fingerprints";
        private int vectorSize = 512;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getGrpcPort() {
            return grpcPort;
        }

        public void setGrpcPort(int grpcPort) {
            this.grpcPort = grpcPort;
        }

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public int getVectorSize() {
            return vectorSize;
        }

        public void setVectorSize(int vectorSize) {
            this.vectorSize = vectorSize;
        }
    }

    public static class Onnx {
        private String modelPath = "classpath:models/fingerprint_embedding.onnx";

        public String getModelPath() {
            return modelPath;
        }

        public void setModelPath(String modelPath) {
            this.modelPath = modelPath;
        }
    }
}
