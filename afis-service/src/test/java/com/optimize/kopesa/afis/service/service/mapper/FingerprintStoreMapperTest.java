package com.optimize.kopesa.afis.service.service.mapper;

import static com.optimize.kopesa.afis.service.domain.FingerprintStoreAsserts.*;
import static com.optimize.kopesa.afis.service.domain.FingerprintStoreTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FingerprintStoreMapperTest {

    private FingerprintStoreMapper fingerprintStoreMapper;

    @BeforeEach
    void setUp() {
        fingerprintStoreMapper = new FingerprintStoreMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFingerprintStoreSample1();
        var actual = fingerprintStoreMapper.toEntity(fingerprintStoreMapper.toDto(expected));
        assertFingerprintStoreAllPropertiesEquals(expected, actual);
    }
}
