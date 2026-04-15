package com.optimize.land.client;

import com.optimize.land.model.dto.BioAuthDto;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.enumeration.BioAuthResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@FeignClient(value = "AFIS-MASTER", url = "http://localhost:8082/api/fingerprint-stores", fallback = AfisClientFallback.class)
public interface AfisClient {

    @PostMapping(value = "bio-auth")
    BioAuthResponse bioAuthRequest(@RequestBody BioAuthDto dto);

    @PostMapping(value = "add-fingerprint-for-entity")
    String sendLegalEntityFingerprint(@RequestBody Set<FingerprintStore> fingerprintStores);

}
