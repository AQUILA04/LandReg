package com.optimize.kopesa.afis.master.domain;

import static com.optimize.kopesa.afis.master.domain.MatcherJobHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.optimize.kopesa.afis.master.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MatcherJobHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatcherJobHistory.class);
        MatcherJobHistory matcherJobHistory1 = getMatcherJobHistorySample1();
        MatcherJobHistory matcherJobHistory2 = new MatcherJobHistory();
        assertThat(matcherJobHistory1).isNotEqualTo(matcherJobHistory2);

        matcherJobHistory2.setId(matcherJobHistory1.getId());
        assertThat(matcherJobHistory1).isEqualTo(matcherJobHistory2);

        matcherJobHistory2 = getMatcherJobHistorySample2();
        assertThat(matcherJobHistory1).isNotEqualTo(matcherJobHistory2);
    }
}
