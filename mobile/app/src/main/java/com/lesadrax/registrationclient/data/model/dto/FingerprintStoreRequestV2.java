package com.lesadrax.registrationclient.data.model.dto;

public class FingerprintStoreRequestV2 {

    private Long id;
    private String fingerStr;
    private Integer partIndex;

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

    public Integer getPartIndex() {
        return partIndex;
    }

    public void setPartIndex(Integer partIndex) {
        this.partIndex = partIndex;
    }
}
