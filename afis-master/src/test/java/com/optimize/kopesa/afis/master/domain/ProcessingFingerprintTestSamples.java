package com.optimize.kopesa.afis.master.domain;

import java.util.UUID;

public class ProcessingFingerprintTestSamples {

    public static ProcessingFingerprint getProcessingFingerprintSample1() {
        return new ProcessingFingerprint().id("id1").rid("rid1");
    }

    public static ProcessingFingerprint getProcessingFingerprintSample2() {
        return new ProcessingFingerprint().id("id2").rid("rid2");
    }

    public static ProcessingFingerprint getProcessingFingerprintRandomSampleGenerator() {
        return new ProcessingFingerprint().id(UUID.randomUUID().toString()).rid(UUID.randomUUID().toString());
    }
}
