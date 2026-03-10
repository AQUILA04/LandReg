package com.optimize.kopesa.afis.master.domain;

import static com.optimize.kopesa.afis.master.domain.ProcessingFingerprintTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.optimize.kopesa.afis.master.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProcessingFingerprintTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProcessingFingerprint.class);
        ProcessingFingerprint processingFingerprint1 = getProcessingFingerprintSample1();
        ProcessingFingerprint processingFingerprint2 = new ProcessingFingerprint();
        assertThat(processingFingerprint1).isNotEqualTo(processingFingerprint2);

        processingFingerprint2.setId(processingFingerprint1.getId());
        assertThat(processingFingerprint1).isEqualTo(processingFingerprint2);

        processingFingerprint2 = getProcessingFingerprintSample2();
        assertThat(processingFingerprint1).isNotEqualTo(processingFingerprint2);
    }
}
