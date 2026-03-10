package com.optimize.kopesa.afis.master.service.mapper;

import static com.optimize.kopesa.afis.master.domain.ProcessingFingerprintAsserts.*;
import static com.optimize.kopesa.afis.master.domain.ProcessingFingerprintTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessingFingerprintMapperTest {

    private ProcessingFingerprintMapper processingFingerprintMapper;

    @BeforeEach
    void setUp() {
        processingFingerprintMapper = new ProcessingFingerprintMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProcessingFingerprintSample1();
        var actual = processingFingerprintMapper.toEntity(processingFingerprintMapper.toDto(expected));
        assertProcessingFingerprintAllPropertiesEquals(expected, actual);
    }
}
