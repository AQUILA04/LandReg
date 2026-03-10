package com.optimize.kopesa.afis.master.web.rest;

import com.optimize.kopesa.afis.master.repository.MatcherJobHistoryRepository;
import com.optimize.kopesa.afis.master.service.MatcherJobHistoryService;
import com.optimize.kopesa.afis.master.service.dto.MatcherJobHistoryDTO;
import com.optimize.kopesa.afis.master.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.optimize.kopesa.afis.master.domain.MatcherJobHistory}.
 */
@RestController
@RequestMapping("/api/matcher-job-histories")
public class MatcherJobHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(MatcherJobHistoryResource.class);

    private static final String ENTITY_NAME = "afisServiceMatcherJobHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MatcherJobHistoryService matcherJobHistoryService;

    private final MatcherJobHistoryRepository matcherJobHistoryRepository;

    public MatcherJobHistoryResource(
        MatcherJobHistoryService matcherJobHistoryService,
        MatcherJobHistoryRepository matcherJobHistoryRepository
    ) {
        this.matcherJobHistoryService = matcherJobHistoryService;
        this.matcherJobHistoryRepository = matcherJobHistoryRepository;
    }

    /**
     * {@code POST  /matcher-job-histories} : Create a new matcherJobHistory.
     *
     * @param matcherJobHistoryDTO the matcherJobHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new matcherJobHistoryDTO, or with status {@code 400 (Bad Request)} if the matcherJobHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MatcherJobHistoryDTO> createMatcherJobHistory(@RequestBody MatcherJobHistoryDTO matcherJobHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MatcherJobHistory : {}", matcherJobHistoryDTO);
        if (matcherJobHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new matcherJobHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        matcherJobHistoryDTO = matcherJobHistoryService.save(matcherJobHistoryDTO);
        return ResponseEntity.created(new URI("/api/matcher-job-histories/" + matcherJobHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, matcherJobHistoryDTO.getId()))
            .body(matcherJobHistoryDTO);
    }

    /**
     * {@code PUT  /matcher-job-histories/:id} : Updates an existing matcherJobHistory.
     *
     * @param id the id of the matcherJobHistoryDTO to save.
     * @param matcherJobHistoryDTO the matcherJobHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated matcherJobHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the matcherJobHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the matcherJobHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MatcherJobHistoryDTO> updateMatcherJobHistory(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody MatcherJobHistoryDTO matcherJobHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MatcherJobHistory : {}, {}", id, matcherJobHistoryDTO);
        if (matcherJobHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, matcherJobHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!matcherJobHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        matcherJobHistoryDTO = matcherJobHistoryService.update(matcherJobHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, matcherJobHistoryDTO.getId()))
            .body(matcherJobHistoryDTO);
    }

    /**
     * {@code PATCH  /matcher-job-histories/:id} : Partial updates given fields of an existing matcherJobHistory, field will ignore if it is null
     *
     * @param id the id of the matcherJobHistoryDTO to save.
     * @param matcherJobHistoryDTO the matcherJobHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated matcherJobHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the matcherJobHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the matcherJobHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the matcherJobHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MatcherJobHistoryDTO> partialUpdateMatcherJobHistory(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody MatcherJobHistoryDTO matcherJobHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MatcherJobHistory partially : {}, {}", id, matcherJobHistoryDTO);
        if (matcherJobHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, matcherJobHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!matcherJobHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MatcherJobHistoryDTO> result = matcherJobHistoryService.partialUpdate(matcherJobHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, matcherJobHistoryDTO.getId())
        );
    }

    /**
     * {@code GET  /matcher-job-histories} : get all the matcherJobHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of matcherJobHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MatcherJobHistoryDTO>> getAllMatcherJobHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of MatcherJobHistories");
        Page<MatcherJobHistoryDTO> page = matcherJobHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /matcher-job-histories/:id} : get the "id" matcherJobHistory.
     *
     * @param id the id of the matcherJobHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the matcherJobHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MatcherJobHistoryDTO> getMatcherJobHistory(@PathVariable("id") String id) {
        LOG.debug("REST request to get MatcherJobHistory : {}", id);
        Optional<MatcherJobHistoryDTO> matcherJobHistoryDTO = matcherJobHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(matcherJobHistoryDTO);
    }

    /**
     * {@code DELETE  /matcher-job-histories/:id} : delete the "id" matcherJobHistory.
     *
     * @param id the id of the matcherJobHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatcherJobHistory(@PathVariable("id") String id) {
        LOG.debug("REST request to delete MatcherJobHistory : {}", id);
        matcherJobHistoryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
