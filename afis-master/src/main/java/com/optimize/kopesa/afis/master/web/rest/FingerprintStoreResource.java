package com.optimize.kopesa.afis.master.web.rest;

import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.service.FingerprintStoreService;
import com.optimize.kopesa.afis.master.service.dto.BioAuthDto;
import com.optimize.kopesa.afis.master.service.dto.BioAuthResponse;
import com.optimize.kopesa.afis.master.service.dto.FingerprintStoreDTO;
import com.optimize.kopesa.afis.master.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
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
 * REST controller for managing {@link com.optimize.kopesa.afis.master.domain.FingerprintStore}.
 */
@RestController
@RequestMapping("/api/fingerprint-stores")
public class FingerprintStoreResource {

    private static final Logger LOG = LoggerFactory.getLogger(FingerprintStoreResource.class);

    private static final String ENTITY_NAME = "afisMasterFingerprintStore";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FingerprintStoreService fingerprintStoreService;

    private final FingerprintStoreRepository fingerprintStoreRepository;

    public FingerprintStoreResource(
        FingerprintStoreService fingerprintStoreService,
        FingerprintStoreRepository fingerprintStoreRepository
    ) {
        this.fingerprintStoreService = fingerprintStoreService;
        this.fingerprintStoreRepository = fingerprintStoreRepository;
    }

    /**
     * {@code POST  /fingerprint-stores} : Create a new fingerprintStore.
     *
     * @param fingerprintStoreDTO the fingerprintStoreDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fingerprintStoreDTO, or with status {@code 400 (Bad Request)} if the fingerprintStore has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FingerprintStoreDTO> createFingerprintStore(@Valid @RequestBody FingerprintStoreDTO fingerprintStoreDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FingerprintStore : {}", fingerprintStoreDTO);
        if (fingerprintStoreDTO.getId() != null) {
            throw new BadRequestAlertException("A new fingerprintStore cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fingerprintStoreDTO = fingerprintStoreService.save(fingerprintStoreDTO);
        return ResponseEntity.created(new URI("/api/fingerprint-stores/" + fingerprintStoreDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, fingerprintStoreDTO.getId()))
            .body(fingerprintStoreDTO);
    }

    @PostMapping("add-fingerprint-for-entity")
    public ResponseEntity<String> addForEntity(@Valid @RequestBody Set<FingerprintStoreDTO> fingerprintStoreDTOs)
        throws URISyntaxException {
        LOG.debug("REST request to add entity FingerprintStore : ");
        String result = fingerprintStoreService.saveEntityFingerprint(fingerprintStoreDTOs);
        return ResponseEntity.ok(result);
    }

    @PostMapping("bio-auth")
    public BioAuthResponse bioAuth(@Valid @RequestBody BioAuthDto dto)
        throws URISyntaxException, ImageWriteException, IOException, ImageReadException {
        LOG.debug("REST request to authenticate : ");
        return fingerprintStoreService.bioAuth(dto);
    }

    /**
     * {@code PUT  /fingerprint-stores/:id} : Updates an existing fingerprintStore.
     *
     * @param id the id of the fingerprintStoreDTO to save.
     * @param fingerprintStoreDTO the fingerprintStoreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fingerprintStoreDTO,
     * or with status {@code 400 (Bad Request)} if the fingerprintStoreDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fingerprintStoreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FingerprintStoreDTO> updateFingerprintStore(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody FingerprintStoreDTO fingerprintStoreDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FingerprintStore : {}, {}", id, fingerprintStoreDTO);
        if (fingerprintStoreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fingerprintStoreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fingerprintStoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fingerprintStoreDTO = fingerprintStoreService.update(fingerprintStoreDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fingerprintStoreDTO.getId()))
            .body(fingerprintStoreDTO);
    }

    /**
     * {@code PATCH  /fingerprint-stores/:id} : Partial updates given fields of an existing fingerprintStore, field will ignore if it is null
     *
     * @param id the id of the fingerprintStoreDTO to save.
     * @param fingerprintStoreDTO the fingerprintStoreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fingerprintStoreDTO,
     * or with status {@code 400 (Bad Request)} if the fingerprintStoreDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fingerprintStoreDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fingerprintStoreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FingerprintStoreDTO> partialUpdateFingerprintStore(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody FingerprintStoreDTO fingerprintStoreDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FingerprintStore partially : {}, {}", id, fingerprintStoreDTO);
        if (fingerprintStoreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fingerprintStoreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fingerprintStoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FingerprintStoreDTO> result = fingerprintStoreService.partialUpdate(fingerprintStoreDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fingerprintStoreDTO.getId())
        );
    }

    /**
     * {@code GET  /fingerprint-stores} : get all the fingerprintStores.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fingerprintStores in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FingerprintStoreDTO>> getAllFingerprintStores(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of FingerprintStores");
        Page<FingerprintStoreDTO> page = fingerprintStoreService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fingerprint-stores/:id} : get the "id" fingerprintStore.
     *
     * @param id the id of the fingerprintStoreDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fingerprintStoreDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FingerprintStoreDTO> getFingerprintStore(@PathVariable("id") String id) {
        LOG.debug("REST request to get FingerprintStore : {}", id);
        Optional<FingerprintStoreDTO> fingerprintStoreDTO = fingerprintStoreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fingerprintStoreDTO);
    }

    /**
     * {@code DELETE  /fingerprint-stores/:id} : delete the "id" fingerprintStore.
     *
     * @param id the id of the fingerprintStoreDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFingerprintStore(@PathVariable("id") String id) {
        LOG.debug("REST request to delete FingerprintStore : {}", id);
        fingerprintStoreService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
