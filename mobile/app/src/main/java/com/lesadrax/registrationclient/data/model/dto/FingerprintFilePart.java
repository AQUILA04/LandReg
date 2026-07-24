package com.lesadrax.registrationclient.data.model.dto;

public class FingerprintFilePart {

    private final String fingerStr;
    private final String filePath;
    private final long fingerId;
    private final int partIndex;

    public FingerprintFilePart(String fingerStr, String filePath, long fingerId, int partIndex) {
        this.fingerStr = fingerStr;
        this.filePath = filePath;
        this.fingerId = fingerId;
        this.partIndex = partIndex;
    }

    public String getFingerStr() {
        return fingerStr;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFingerId() {
        return fingerId;
    }

    public int getPartIndex() {
        return partIndex;
    }
}
