package com.optimize.common.blob.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FingerRef {

    private String fingerId;
    private String handType;
    private String fingerName;
    private String contentType;
    private String imageUri;
    private String imageObjectKey;
    private String imageBucket;

    public FingerRef() {}

    public FingerRef(
        String fingerId,
        String handType,
        String fingerName,
        String contentType,
        String imageUri,
        String imageObjectKey,
        String imageBucket
    ) {
        this.fingerId = fingerId;
        this.handType = handType;
        this.fingerName = fingerName;
        this.contentType = contentType;
        this.imageUri = imageUri;
        this.imageObjectKey = imageObjectKey;
        this.imageBucket = imageBucket;
    }

    public String getFingerId() {
        return fingerId;
    }

    public void setFingerId(String fingerId) {
        this.fingerId = fingerId;
    }

    public String getHandType() {
        return handType;
    }

    public void setHandType(String handType) {
        this.handType = handType;
    }

    public String getFingerName() {
        return fingerName;
    }

    public void setFingerName(String fingerName) {
        this.fingerName = fingerName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageObjectKey() {
        return imageObjectKey;
    }

    public void setImageObjectKey(String imageObjectKey) {
        this.imageObjectKey = imageObjectKey;
    }

    public String getImageBucket() {
        return imageBucket;
    }

    public void setImageBucket(String imageBucket) {
        this.imageBucket = imageBucket;
    }
}
