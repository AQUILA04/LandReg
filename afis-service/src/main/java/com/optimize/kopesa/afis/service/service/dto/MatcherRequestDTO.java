package com.optimize.kopesa.afis.service.service.dto;

import com.machinezoo.sourceafis.FingerprintMatcher;

import java.util.List;

public class MatcherRequestDTO {
    private int batchId;
    private String rid;
    private int batchSize;
    private double threshold;
    private List<FingerprintMatcher> matchers;

    public MatcherRequestDTO() {
    }

    public MatcherRequestDTO(int batchId, String rid, int batchSize, double threshold, List<FingerprintMatcher> matchers) {
        this.batchId = batchId;
        this.rid = rid;
        this.batchSize = batchSize;
        this.threshold = threshold;
        this.matchers = matchers;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public List<FingerprintMatcher> getMatchers() {
        return matchers;
    }

    public void setMatchers(List<FingerprintMatcher> matchers) {
        this.matchers = matchers;
    }
}
