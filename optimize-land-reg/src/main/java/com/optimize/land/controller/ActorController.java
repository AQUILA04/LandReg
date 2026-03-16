package com.optimize.land.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.controller.BaseController;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.dto.*;
import com.optimize.land.model.entity.AbstractActor;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.service.ActorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("land-reg/api/v1/actors")
@Slf4j
public class ActorController extends BaseController<AbstractActor, Long> {

    public ActorController(CustomMessageSource messageSource, ActorService service) {
        super(messageSource, service);
    }

    @PostMapping
    public ResponseEntity<Response> register(@RequestBody @Valid ActorDto actorDto) throws JsonProcessingException {
        log.info("REGISTER ACTOR REQUEST: {}", actorDto);
        return new ResponseEntity<>(success(getService().register(actorDto), "Actor register successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public ResponseEntity<Response> getAll(Pageable pageable) {
            log.info("GET ALL ACTORS REQUEST PAGEABLE:");
        return new ResponseEntity<>(success(getService().getAllActors(pageable), "Get All actor success"), HttpStatus.OK);
    }

    @GetMapping(value = "all")
    @Override
    public ResponseEntity<Response> getAll() {
        log.info("GET ALL ACTORS REQUEST:");
        return new ResponseEntity<>(success(getService().getByStatus(RegistrationStatus.ACTOR), "Get All actor success"), HttpStatus.OK);
    }

    @GetMapping(value = "by-status")
    public ResponseEntity<Response> getAllByStatus(RegistrationStatus status, Pageable pageable) {
        log.info("GET ALL ACTORS REQUEST BY STATUS: {}", status);
        return new ResponseEntity<>(success(getService().getByStatus(status, pageable), "Successful get all actor by status"), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    @Override
    public ResponseEntity<Response> getOne(@PathVariable Long id) {
        log.info("GET ACTOR REQUEST BY ID: {}", id);
        return super.getOne(id);
    }

    @DeleteMapping(value = "{id}")
    @Override
    public ResponseEntity<Response> deleteSoft(@PathVariable Long id) {
        log.info("DELETE ACTOR REQUEST BY ID: {}", id);
        return super.deleteSoft(id);
    }

    @PostMapping(value = "bio-auth")
    public ResponseEntity<Response> bioAuthentication(@RequestBody @Valid BioAuthDto dto) {
        log.info("BIOMETRIC AUTHENTICATION REQUEST: {}", dto);
        return new ResponseEntity<>(success(getService().bioAuth(dto), "success bio authentication"), HttpStatus.OK);
    }

    @PostMapping(value = "uin-details")
    public ResponseEntity<Response> getUINDetails(@RequestBody @Valid UINWrapper uinWrapper) {
        log.info("GET UIN DETAILS REQUEST: {}", uinWrapper);
        return new ResponseEntity<>(success(getService().getUINDetails(uinWrapper), "success get UIN details"), HttpStatus.OK);
    }

    @GetMapping(value = "uin/{uin}")
    public ResponseEntity<Response> getUIN(@PathVariable String uin) {
        log.info("GET UIN DETAILS REQUEST: {}", uin);
        UINWrapper uinWrapper = new UINWrapper();
        uinWrapper.setUinList(Set.of(uin));
        Optional<ActorModel> optional = getService().getUINDetails(uinWrapper).stream().findFirst();
        return new ResponseEntity<>(success(optional.orElse(null), "success get UIN details"), HttpStatus.OK);
    }

    @PostMapping(value = "search")
    public ResponseEntity<Response> search(@RequestBody @Valid SearchDto dto, Pageable pageable) {
        log.info("SEARCH REQUEST: {}", dto);
        return new ResponseEntity<>(success(getService().search(dto.keyword(), pageable), "success search by keyword"), HttpStatus.OK);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<Response> update(@PathVariable Long id, @RequestBody @Valid ActorDto dto) {
        log.info("UPDATE ACTOR REQUEST: {}", dto);
        return new ResponseEntity<>(success(getService().updateActor(dto, id), "success update actor"), HttpStatus.OK);
    }

    public ActorService getService() {
        return (ActorService) service;
    }
}
