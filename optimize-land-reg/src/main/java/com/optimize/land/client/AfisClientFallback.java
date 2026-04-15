package com.optimize.land.client;

import com.optimize.land.model.dto.BioAuthDto;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.enumeration.BioAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class AfisClientFallback implements AfisClient {

    @Override
    public BioAuthResponse bioAuthRequest(BioAuthDto dto) {
        log.error("AFIS-MASTER is unreachable. Fallback triggered for bioAuthRequest: {}", dto.getUin());
        // Return a default response indicating the service is unavailable
        return BioAuthResponse.FINGERPRINT_NOT_MATCH;
    }

    @Override
    public String sendLegalEntityFingerprint(Set<FingerprintStore> fingerprintStores) {
        log.error("AFIS-MASTER is unreachable. Fallback triggered for sendLegalEntityFingerprint");
        // We could implement an event publish here to save in a retry queue
        return "FALLBACK_PENDING";
    }
}
