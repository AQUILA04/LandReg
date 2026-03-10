package com.optimize.kopesa.afis.master.domain;

import java.util.UUID;

public class FingerprintStoreTestSamples {

    public static FingerprintStore getFingerprintStoreSample1() {
        return new FingerprintStore().id("id1").rid("rid1");
    }

    public static FingerprintStore getFingerprintStoreSample2() {
        return new FingerprintStore().id("id2").rid("rid2");
    }

    public static FingerprintStore getFingerprintStoreRandomSampleGenerator() {
        return new FingerprintStore().id(UUID.randomUUID().toString()).rid(UUID.randomUUID().toString());
    }
}
