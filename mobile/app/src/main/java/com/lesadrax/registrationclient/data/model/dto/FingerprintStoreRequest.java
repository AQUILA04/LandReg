package com.lesadrax.registrationclient.data.model.dto;

public class FingerprintStoreRequest {

    private Long id;
    private String fingerStr;
    private String fingerprintImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFingerStr() {
        return fingerStr;
    }

    public void setFingerStr(String fingerStr) {
        this.fingerStr = fingerStr;
    }

    public String getFingerprintImage() {
        return fingerprintImage;
    }

    public void setFingerprintImage(String fingerprintImage) {
        this.fingerprintImage = fingerprintImage;
    }
}
