package com.optimize.land.controller;

import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.controller.BaseController;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.entity.FingerprintMatchingHistory;
import com.optimize.land.service.FingerprintMatchingHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/land-reg/fingerprint-matching-histories")
@Slf4j
public class FingerprintMatchingHistoryController extends BaseController<FingerprintMatchingHistory, Long> {

    public FingerprintMatchingHistoryController(CustomMessageSource messageSource, FingerprintMatchingHistoryService service) {
        super(messageSource, service);
    }

    @Override
    public ResponseEntity<Response> getAll(Pageable pageable) {
        return super.getAll(pageable);
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
}
