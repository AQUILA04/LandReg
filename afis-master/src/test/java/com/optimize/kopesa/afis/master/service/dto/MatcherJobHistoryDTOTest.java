package com.optimize.kopesa.afis.master.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.optimize.kopesa.afis.master.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MatcherJobHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatcherJobHistoryDTO.class);
        MatcherJobHistoryDTO matcherJobHistoryDTO1 = new MatcherJobHistoryDTO();
        matcherJobHistoryDTO1.setId("id1");
        MatcherJobHistoryDTO matcherJobHistoryDTO2 = new MatcherJobHistoryDTO();
        assertThat(matcherJobHistoryDTO1).isNotEqualTo(matcherJobHistoryDTO2);
        matcherJobHistoryDTO2.setId(matcherJobHistoryDTO1.getId());
        assertThat(matcherJobHistoryDTO1).isEqualTo(matcherJobHistoryDTO2);
        matcherJobHistoryDTO2.setId("id2");
        assertThat(matcherJobHistoryDTO1).isNotEqualTo(matcherJobHistoryDTO2);
        matcherJobHistoryDTO1.setId(null);
        assertThat(matcherJobHistoryDTO1).isNotEqualTo(matcherJobHistoryDTO2);
    }
}
