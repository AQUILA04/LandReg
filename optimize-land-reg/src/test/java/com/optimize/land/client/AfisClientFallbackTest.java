package com.optimize.land.client;

import com.optimize.land.model.dto.BioAuthDto;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.enumeration.BioAuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AfisClientFallbackTest {

    private AfisClientFallback fallback;

    @BeforeEach
    void setUp() {
        fallback = new AfisClientFallback();
    }

    @Test
    void bioAuthRequest_ReturnsFingerprintNotMatch() {
        // Arrange
        BioAuthDto dto = new BioAuthDto();
        dto.setUin("UIN-123");

        // Act
        BioAuthResponse response = fallback.bioAuthRequest(dto);

        // Assert
        assertEquals(BioAuthResponse.FINGERPRINT_NOT_MATCH, response);
    }

    @Test
    void sendLegalEntityFingerprint_ReturnsFallbackPending() {
        // Arrange
        Set<FingerprintStore> fingerprintStores = new HashSet<>();

        // Act
        String response = fallback.sendLegalEntityFingerprint(fingerprintStores);

        // Assert
        assertEquals("FALLBACK_PENDING", response);
    }
}
