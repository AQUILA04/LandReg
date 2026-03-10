package com.optimize.kopesa.afis.master.service;

import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.dto.ProcessingFingerprintDTO;
import com.optimize.kopesa.afis.master.service.mapper.ProcessingFingerprintMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.optimize.kopesa.afis.master.domain.ProcessingFingerprint}.
 */
@Service
public class ProcessingFingerprintService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingFingerprintService.class);

    private final ProcessingFingerprintRepository processingFingerprintRepository;

    private final ProcessingFingerprintMapper processingFingerprintMapper;

    public ProcessingFingerprintService(
        ProcessingFingerprintRepository processingFingerprintRepository,
        ProcessingFingerprintMapper processingFingerprintMapper
    ) {
        this.processingFingerprintRepository = processingFingerprintRepository;
        this.processingFingerprintMapper = processingFingerprintMapper;
    }

    /**
     * Save a processingFingerprint.
     *
     * @param processingFingerprintDTO the entity to save.
     * @return the persisted entity.
     */
    public ProcessingFingerprintDTO save(ProcessingFingerprintDTO processingFingerprintDTO) {
        LOG.debug("Request to save ProcessingFingerprint : {}", processingFingerprintDTO);
        ProcessingFingerprint processingFingerprint = processingFingerprintMapper.toEntity(processingFingerprintDTO);
        processingFingerprint = processingFingerprintRepository.save(processingFingerprint);
        return processingFingerprintMapper.toDto(processingFingerprint);
    }

    /**
     * Update a processingFingerprint.
     *
     * @param processingFingerprintDTO the entity to save.
     * @return the persisted entity.
     */
    public ProcessingFingerprintDTO update(ProcessingFingerprintDTO processingFingerprintDTO) {
        LOG.debug("Request to update ProcessingFingerprint : {}", processingFingerprintDTO);
        ProcessingFingerprint processingFingerprint = processingFingerprintMapper.toEntity(processingFingerprintDTO);
        processingFingerprint = processingFingerprintRepository.save(processingFingerprint);
        return processingFingerprintMapper.toDto(processingFingerprint);
    }

    /**
     * Partially update a processingFingerprint.
     *
     * @param processingFingerprintDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProcessingFingerprintDTO> partialUpdate(ProcessingFingerprintDTO processingFingerprintDTO) {
        LOG.debug("Request to partially update ProcessingFingerprint : {}", processingFingerprintDTO);

        return processingFingerprintRepository
            .findById(processingFingerprintDTO.getId())
            .map(existingProcessingFingerprint -> {
                processingFingerprintMapper.partialUpdate(existingProcessingFingerprint, processingFingerprintDTO);

                return existingProcessingFingerprint;
            })
            .map(processingFingerprintRepository::save)
            .map(processingFingerprintMapper::toDto);
    }

    /**
     * Get all the processingFingerprints.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<ProcessingFingerprintDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProcessingFingerprints");
        return processingFingerprintRepository.findAll(pageable).map(processingFingerprintMapper::toDto);
    }

    /**
     * Get one processingFingerprint by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ProcessingFingerprintDTO> findOne(String id) {
        LOG.debug("Request to get ProcessingFingerprint : {}", id);
        return processingFingerprintRepository.findById(id).map(processingFingerprintMapper::toDto);
    }

    /**
     * Delete the processingFingerprint by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete ProcessingFingerprint : {}", id);
        processingFingerprintRepository.deleteById(id);
    }
}
