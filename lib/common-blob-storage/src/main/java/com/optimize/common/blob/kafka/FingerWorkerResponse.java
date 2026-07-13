package com.optimize.common.blob.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FingerWorkerResponse {

    private String rid;
    private String fingerId;
    private FingerMatchStatus status;
    private String matchedRid;
    private String fingerprintTemplateBase64;
    private String qdrantPointId;
    private double highestScore;
    private PipelineTimings timings;
    private String errorMessage;

    public FingerWorkerResponse() {}

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

    public FingerMatchStatus getStatus() {
        return status;
    }

    public void setStatus(FingerMatchStatus status) {
        this.status = status;
    }

    public String getMatchedRid() {
        return matchedRid;
    }

    public void setMatchedRid(String matchedRid) {
        this.matchedRid = matchedRid;
    }

    public String getFingerprintTemplateBase64() {
        return fingerprintTemplateBase64;
    }

    public void setFingerprintTemplateBase64(String fingerprintTemplateBase64) {
        this.fingerprintTemplateBase64 = fingerprintTemplateBase64;
    }

    public String getQdrantPointId() {
        return qdrantPointId;
    }

    public void setQdrantPointId(String qdrantPointId) {
        this.qdrantPointId = qdrantPointId;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(double highestScore) {
        this.highestScore = highestScore;
    }

    public PipelineTimings getTimings() {
        return timings;
    }

    public void setTimings(PipelineTimings timings) {
        this.timings = timings;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
