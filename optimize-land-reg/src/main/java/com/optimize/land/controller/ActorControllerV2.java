package com.optimize.land.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.controller.BaseController;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.dto.v2.ActorDtoV2;
import com.optimize.land.model.entity.AbstractActor;
import com.optimize.land.service.ActorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("land-reg/api/v2/actors")
@Slf4j
public class ActorControllerV2 extends BaseController<AbstractActor, Long> {

    public ActorControllerV2(CustomMessageSource messageSource, ActorService service) {
        super(messageSource, service);
    }

    /**
     * Multipart registration endpoint.
     * Parts:
     * - actor: JSON metadata (ActorDtoV2) without base64 fingerprint images
     * - fingerprints: binary image files in the same order as fingerprintStores metadata
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> register(
        @RequestPart("actor") @Valid ActorDtoV2 actorDto,
        @RequestPart(value = "fingerprints", required = false) List<MultipartFile> fingerprintFiles
    ) throws JsonProcessingException {
        log.info(
            "REGISTER ACTOR V2 REQUEST: batch={} files={}",
            actorDto.getSynchroBatchNumber(),
            fingerprintFiles != null ? fingerprintFiles.size() : 0
        );
        actorDto.validateUniqueActorType();
        List<MultipartFile> files = fingerprintFiles != null ? fingerprintFiles : List.of();
        return new ResponseEntity<>(success(getService().registerV2(actorDto, files), "Actor register successfully"), HttpStatus.CREATED);
    }

    public ActorService getService() {
        return (ActorService) service;
    }
}
