package com.optimize.kopesa.afis.master.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.optimize.kopesa.afis.master.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FingerprintStoreDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FingerprintStoreDTO.class);
        FingerprintStoreDTO fingerprintStoreDTO1 = new FingerprintStoreDTO();
        fingerprintStoreDTO1.setId("id1");
        FingerprintStoreDTO fingerprintStoreDTO2 = new FingerprintStoreDTO();
        assertThat(fingerprintStoreDTO1).isNotEqualTo(fingerprintStoreDTO2);
        fingerprintStoreDTO2.setId(fingerprintStoreDTO1.getId());
        assertThat(fingerprintStoreDTO1).isEqualTo(fingerprintStoreDTO2);
        fingerprintStoreDTO2.setId("id2");
        assertThat(fingerprintStoreDTO1).isNotEqualTo(fingerprintStoreDTO2);
        fingerprintStoreDTO1.setId(null);
        assertThat(fingerprintStoreDTO1).isNotEqualTo(fingerprintStoreDTO2);
    }
}
