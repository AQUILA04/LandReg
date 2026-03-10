package com.optimize.kopesa.afis.service.service;

import com.optimize.kopesa.afis.service.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.service.repository.MatcherJobHistoryRepository;
import com.optimize.kopesa.afis.service.service.dto.MatcherJobHistoryDTO;
import com.optimize.kopesa.afis.service.service.mapper.MatcherJobHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.optimize.kopesa.afis.service.domain.MatcherJobHistory}.
 */
@Service
public class MatcherJobHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(MatcherJobHistoryService.class);

    private final MatcherJobHistoryRepository matcherJobHistoryRepository;

    private final MatcherJobHistoryMapper matcherJobHistoryMapper;

    public MatcherJobHistoryService(
        MatcherJobHistoryRepository matcherJobHistoryRepository,
        MatcherJobHistoryMapper matcherJobHistoryMapper
    ) {
        this.matcherJobHistoryRepository = matcherJobHistoryRepository;
        this.matcherJobHistoryMapper = matcherJobHistoryMapper;
    }

    /**
     * Save a matcherJobHistory.
     *
     * @param matcherJobHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public MatcherJobHistoryDTO save(MatcherJobHistoryDTO matcherJobHistoryDTO) {
        LOG.debug("Request to save MatcherJobHistory : {}", matcherJobHistoryDTO);
        MatcherJobHistory matcherJobHistory = matcherJobHistoryMapper.toEntity(matcherJobHistoryDTO);
        matcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);
        return matcherJobHistoryMapper.toDto(matcherJobHistory);
    }

    /**
     * Update a matcherJobHistory.
     *
     * @param matcherJobHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public MatcherJobHistoryDTO update(MatcherJobHistoryDTO matcherJobHistoryDTO) {
        LOG.debug("Request to update MatcherJobHistory : {}", matcherJobHistoryDTO);
        MatcherJobHistory matcherJobHistory = matcherJobHistoryMapper.toEntity(matcherJobHistoryDTO);
        matcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);
        return matcherJobHistoryMapper.toDto(matcherJobHistory);
    }

    /**
     * Partially update a matcherJobHistory.
     *
     * @param matcherJobHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MatcherJobHistoryDTO> partialUpdate(MatcherJobHistoryDTO matcherJobHistoryDTO) {
        LOG.debug("Request to partially update MatcherJobHistory : {}", matcherJobHistoryDTO);

        return matcherJobHistoryRepository
            .findById(matcherJobHistoryDTO.getId())
            .map(existingMatcherJobHistory -> {
                matcherJobHistoryMapper.partialUpdate(existingMatcherJobHistory, matcherJobHistoryDTO);

                return existingMatcherJobHistory;
            })
            .map(matcherJobHistoryRepository::save)
            .map(matcherJobHistoryMapper::toDto);
    }

    /**
     * Get all the matcherJobHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<MatcherJobHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MatcherJobHistories");
        return matcherJobHistoryRepository.findAll(pageable).map(matcherJobHistoryMapper::toDto);
    }

    /**
     * Get one matcherJobHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<MatcherJobHistoryDTO> findOne(String id) {
        LOG.debug("Request to get MatcherJobHistory : {}", id);
        return matcherJobHistoryRepository.findById(id).map(matcherJobHistoryMapper::toDto);
    }

    /**
     * Delete the matcherJobHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete MatcherJobHistory : {}", id);
        matcherJobHistoryRepository.deleteById(id);
    }
}
