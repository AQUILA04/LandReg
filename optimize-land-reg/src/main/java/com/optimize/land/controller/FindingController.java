package com.optimize.land.controller;

import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.controller.BaseController;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.dto.FindingDto;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.service.FindingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("land-reg/api/v1/constatations")
public class FindingController extends BaseController<Finding, Long> {
    public FindingController(CustomMessageSource messageSource,
                             FindingService service) {
        super(messageSource, service);
    }

    @PostMapping
    public ResponseEntity<Response> create(@RequestBody @Valid FindingDto findingDto) {
        return new ResponseEntity<>(success(getService().register(findingDto), "Constatation create success", HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response> update(@RequestBody @Valid FindingDto findingDto) {
        return new ResponseEntity<>(success(getService().updateFinding(findingDto), "Constatation update success", HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping
    @Override
    public ResponseEntity<Response> getAll(Pageable pageable) {
        return new ResponseEntity<>(success(getService().getAllToProjection(pageable), "Constatation get all success"), HttpStatus.OK);
    }

    @GetMapping(value = "all")
    @Override
    public ResponseEntity<Response> getAll() {
        return super.getAll();
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

    @Override
    public FindingService getService() {
        return (FindingService) super.getService();
    }
}
