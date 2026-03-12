package com.optimize.land.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.controller.BaseController;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.dto.ActorDto;
import com.optimize.land.model.dto.BioAuthDto;
import com.optimize.land.model.dto.UINWrapper;
import com.optimize.land.model.entity.AbstractActor;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.service.ActorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("land-reg/api/v1/actors")
public class ActorController extends BaseController<AbstractActor, Long> {

    public ActorController(CustomMessageSource messageSource, ActorService service) {
        super(messageSource, service);
    }

    @PostMapping
    public ResponseEntity<Response> register(@RequestBody @Valid ActorDto actorDto) throws JsonProcessingException {
        return new ResponseEntity<>(success(getService().register(actorDto), "Actor register successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public ResponseEntity<Response> getAll(Pageable pageable) {
        return new ResponseEntity<>(success(getService().getAllActors(pageable), "Get All actor success"), HttpStatus.OK);
    }

    @GetMapping(value = "all")
    @Override
    public ResponseEntity<Response> getAll() {
        return new ResponseEntity<>(success(getService().getByStatus(RegistrationStatus.ACTOR), "Get All actor success"), HttpStatus.OK);
    }

    @GetMapping(value = "by-status")
    public ResponseEntity<Response> getAllByStatus(RegistrationStatus status, Pageable pageable) {
        return new ResponseEntity<>(success(getService().getByStatus(status, pageable), "Successful get all actor by status"), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    @Override
    public ResponseEntity<Response> getOne(@PathVariable Long id) {
        return super.getOne(id);
    }

    @DeleteMapping(value = "{id}")
    @Override
    public ResponseEntity<Response> deleteSoft(@PathVariable Long id) {
        return super.deleteSoft(id);
    }

    @PostMapping(value = "bio-auth")
    public ResponseEntity<Response> bioAuthentication(@RequestBody @Valid BioAuthDto dto) {
        return new ResponseEntity<>(success(getService().bioAuth(dto), "success bio authentication"), HttpStatus.OK);
    }

    @PostMapping(value = "uin-details")
    public ResponseEntity<Response> getUINDetails(@RequestBody @Valid UINWrapper uinWrapper) {
        return new ResponseEntity<>(success(getService().getUINDetails(uinWrapper), "success get UIN details"), HttpStatus.OK);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<Response> update(@PathVariable Long id, @RequestBody @Valid ActorDto dto) {
        return new ResponseEntity<>(success(getService().updateActor(dto, id), "success update actor"), HttpStatus.OK);
    }

    public ActorService getService() {
        return (ActorService) service;
    }
}
