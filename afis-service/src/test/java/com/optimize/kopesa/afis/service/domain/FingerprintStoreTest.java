package com.optimize.kopesa.afis.service.domain;

import static com.optimize.kopesa.afis.service.domain.FingerprintStoreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.optimize.kopesa.afis.service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FingerprintStoreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FingerprintStore.class);
        FingerprintStore fingerprintStore1 = getFingerprintStoreSample1();
        FingerprintStore fingerprintStore2 = new FingerprintStore();
        assertThat(fingerprintStore1).isNotEqualTo(fingerprintStore2);

        fingerprintStore2.setId(fingerprintStore1.getId());
        assertThat(fingerprintStore1).isEqualTo(fingerprintStore2);

        fingerprintStore2 = getFingerprintStoreSample2();
        assertThat(fingerprintStore1).isNotEqualTo(fingerprintStore2);
    }
}
