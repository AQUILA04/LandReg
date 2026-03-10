package com.optimize.kopesa.afis.master.web.rest;

import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.ProcessingFingerprintService;
import com.optimize.kopesa.afis.master.service.dto.ProcessingFingerprintDTO;
import com.optimize.kopesa.afis.master.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.optimize.kopesa.afis.master.domain.ProcessingFingerprint}.
 */
@RestController
@RequestMapping("/api/processing-fingerprints")
public class ProcessingFingerprintResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingFingerprintResource.class);

    private static final String ENTITY_NAME = "afisMasterProcessingFingerprint";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProcessingFingerprintService processingFingerprintService;

    private final ProcessingFingerprintRepository processingFingerprintRepository;

    public ProcessingFingerprintResource(
        ProcessingFingerprintService processingFingerprintService,
        ProcessingFingerprintRepository processingFingerprintRepository
    ) {
        this.processingFingerprintService = processingFingerprintService;
        this.processingFingerprintRepository = processingFingerprintRepository;
    }

    /**
     * {@code POST  /processing-fingerprints} : Create a new processingFingerprint.
     *
     * @param processingFingerprintDTO the processingFingerprintDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new processingFingerprintDTO, or with status {@code 400 (Bad Request)} if the processingFingerprint has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProcessingFingerprintDTO> createProcessingFingerprint(
        @Valid @RequestBody ProcessingFingerprintDTO processingFingerprintDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ProcessingFingerprint : {}", processingFingerprintDTO);
        if (processingFingerprintDTO.getId() != null) {
            throw new BadRequestAlertException("A new processingFingerprint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        processingFingerprintDTO = processingFingerprintService.save(processingFingerprintDTO);
        return ResponseEntity.created(new URI("/api/processing-fingerprints/" + processingFingerprintDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, processingFingerprintDTO.getId()))
            .body(processingFingerprintDTO);
    }

    /**
     * {@code PUT  /processing-fingerprints/:id} : Updates an existing processingFingerprint.
     *
     * @param id the id of the processingFingerprintDTO to save.
     * @param processingFingerprintDTO the processingFingerprintDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated processingFingerprintDTO,
     * or with status {@code 400 (Bad Request)} if the processingFingerprintDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the processingFingerprintDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProcessingFingerprintDTO> updateProcessingFingerprint(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ProcessingFingerprintDTO processingFingerprintDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProcessingFingerprint : {}, {}", id, processingFingerprintDTO);
        if (processingFingerprintDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, processingFingerprintDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!processingFingerprintRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        processingFingerprintDTO = processingFingerprintService.update(processingFingerprintDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, processingFingerprintDTO.getId()))
            .body(processingFingerprintDTO);
    }

    /**
     * {@code PATCH  /processing-fingerprints/:id} : Partial updates given fields of an existing processingFingerprint, field will ignore if it is null
     *
     * @param id the id of the processingFingerprintDTO to save.
     * @param processingFingerprintDTO the processingFingerprintDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated processingFingerprintDTO,
     * or with status {@code 400 (Bad Request)} if the processingFingerprintDTO is not valid,
     * or with status {@code 404 (Not Found)} if the processingFingerprintDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the processingFingerprintDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProcessingFingerprintDTO> partialUpdateProcessingFingerprint(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ProcessingFingerprintDTO processingFingerprintDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProcessingFingerprint partially : {}, {}", id, processingFingerprintDTO);
        if (processingFingerprintDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, processingFingerprintDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!processingFingerprintRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProcessingFingerprintDTO> result = processingFingerprintService.partialUpdate(processingFingerprintDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, processingFingerprintDTO.getId())
        );
    }

    /**
     * {@code GET  /processing-fingerprints} : get all the processingFingerprints.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of processingFingerprints in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProcessingFingerprintDTO>> getAllProcessingFingerprints(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ProcessingFingerprints");
        Page<ProcessingFingerprintDTO> page = processingFingerprintService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /processing-fingerprints/:id} : get the "id" processingFingerprint.
     *
     * @param id the id of the processingFingerprintDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the processingFingerprintDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProcessingFingerprintDTO> getProcessingFingerprint(@PathVariable("id") String id) {
        LOG.debug("REST request to get ProcessingFingerprint : {}", id);
        Optional<ProcessingFingerprintDTO> processingFingerprintDTO = processingFingerprintService.findOne(id);
        return ResponseUtil.wrapOrNotFound(processingFingerprintDTO);
    }

    /**
     * {@code DELETE  /processing-fingerprints/:id} : delete the "id" processingFingerprint.
     *
     * @param id the id of the processingFingerprintDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcessingFingerprint(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ProcessingFingerprint : {}", id);
        processingFingerprintService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
