package com.optimize.kopesa.afis.master.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.optimize.kopesa.afis.master.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProcessingFingerprintDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProcessingFingerprintDTO.class);
        ProcessingFingerprintDTO processingFingerprintDTO1 = new ProcessingFingerprintDTO();
        processingFingerprintDTO1.setId("id1");
        ProcessingFingerprintDTO processingFingerprintDTO2 = new ProcessingFingerprintDTO();
        assertThat(processingFingerprintDTO1).isNotEqualTo(processingFingerprintDTO2);
        processingFingerprintDTO2.setId(processingFingerprintDTO1.getId());
        assertThat(processingFingerprintDTO1).isEqualTo(processingFingerprintDTO2);
        processingFingerprintDTO2.setId("id2");
        assertThat(processingFingerprintDTO1).isNotEqualTo(processingFingerprintDTO2);
        processingFingerprintDTO1.setId(null);
        assertThat(processingFingerprintDTO1).isNotEqualTo(processingFingerprintDTO2);
    }
}
