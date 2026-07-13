package com.optimize.common.blob.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineTimings {

    private long s3FetchMs;
    private long vectorizeMs;
    private long hnswMs;
    private long sourceafisMs;

    public PipelineTimings() {}

    public long getS3FetchMs() {
        return s3FetchMs;
    }

    public void setS3FetchMs(long s3FetchMs) {
        this.s3FetchMs = s3FetchMs;
    }

    public long getVectorizeMs() {
        return vectorizeMs;
    }

    public void setVectorizeMs(long vectorizeMs) {
        this.vectorizeMs = vectorizeMs;
    }

    public long getHnswMs() {
        return hnswMs;
    }

    public void setHnswMs(long hnswMs) {
        this.hnswMs = hnswMs;
    }

    public long getSourceafisMs() {
        return sourceafisMs;
    }

    public void setSourceafisMs(long sourceafisMs) {
        this.sourceafisMs = sourceafisMs;
    }
}
