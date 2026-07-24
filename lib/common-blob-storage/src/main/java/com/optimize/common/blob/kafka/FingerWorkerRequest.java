package com.optimize.common.blob.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FingerWorkerRequest {

    private String rid;
    private String fingerId;
    private String imageUri;
    private String correlationId;
    private double threshold = 65d;

    public FingerWorkerRequest() {}

    public FingerWorkerRequest(String rid, String fingerId, String imageUri, String correlationId) {
        this.rid = rid;
        this.fingerId = fingerId;
        this.imageUri = imageUri;
        this.correlationId = correlationId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getFingerId() {
        return fingerId;
    }

    public void setFingerId(String fingerId) {
        this.fingerId = fingerId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
