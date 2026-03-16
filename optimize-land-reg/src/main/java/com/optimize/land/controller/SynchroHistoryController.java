package com.optimize.land.controller;


import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.controller.BaseController;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.dto.SynchroHistoryDto;
import com.optimize.land.model.entity.SynchroHistory;
import com.optimize.land.service.SynchroHistoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("land-reg/api/v1/synchro-histories")
@Slf4j
public class SynchroHistoryController extends BaseController<SynchroHistory, Long> {

    public SynchroHistoryController(CustomMessageSource messageSource,
                                    SynchroHistoryService service) {
        super(messageSource, service);
    }

    @PostMapping(value = "init-synchro")
    public ResponseEntity<Response> initSynchro(@RequestBody @Valid SynchroHistoryDto dto) {
        log.info("INIT SYNCHRO REQUEST: {}", dto);
        return new ResponseEntity<>(success(((SynchroHistoryService) service).initSynchro(dto), "init synchro success"), HttpStatus.OK);
    }

    @PatchMapping(value = "finish-synchro/{batch-number}")
    public ResponseEntity<Response> finishSynchro(@PathVariable("batch-number") String batchNumber) {
        log.info("FINISH SYNCHRO REQUEST: {}", batchNumber);
        return new ResponseEntity<>(success(((SynchroHistoryService) service).finishSynchro(batchNumber), "finish synchro success"), HttpStatus.OK);
    }

    @GetMapping
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

    @Override
    public SynchroHistoryService getService() {
        return (SynchroHistoryService) super.getService();
    }
}
