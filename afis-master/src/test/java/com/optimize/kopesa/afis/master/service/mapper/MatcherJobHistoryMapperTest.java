package com.optimize.kopesa.afis.master.service.mapper;

import static com.optimize.kopesa.afis.master.domain.MatcherJobHistoryAsserts.*;
import static com.optimize.kopesa.afis.master.domain.MatcherJobHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatcherJobHistoryMapperTest {

    private MatcherJobHistoryMapper matcherJobHistoryMapper;

    @BeforeEach
    void setUp() {
        matcherJobHistoryMapper = new MatcherJobHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMatcherJobHistorySample1();
        var actual = matcherJobHistoryMapper.toEntity(matcherJobHistoryMapper.toDto(expected));
        assertMatcherJobHistoryAllPropertiesEquals(expected, actual);
    }
}
