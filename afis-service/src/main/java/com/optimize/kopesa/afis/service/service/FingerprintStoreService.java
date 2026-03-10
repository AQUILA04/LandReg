package com.optimize.kopesa.afis.service.service;

import com.optimize.kopesa.afis.service.domain.FingerprintStore;
import com.optimize.kopesa.afis.service.domain.enumeration.ActorType;
import com.optimize.kopesa.afis.service.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.service.service.dto.FingerprintStoreDTO;
import com.optimize.kopesa.afis.service.service.mapper.FingerprintStoreMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.optimize.kopesa.afis.service.domain.FingerprintStore}.
 */
@Service
public class FingerprintStoreService {

    private static final Logger LOG = LoggerFactory.getLogger(FingerprintStoreService.class);

    private final FingerprintStoreRepository fingerprintStoreRepository;

    private final FingerprintStoreMapper fingerprintStoreMapper;

    public FingerprintStoreService(FingerprintStoreRepository fingerprintStoreRepository, FingerprintStoreMapper fingerprintStoreMapper) {
        this.fingerprintStoreRepository = fingerprintStoreRepository;
        this.fingerprintStoreMapper = fingerprintStoreMapper;
    }

    /**
     * Save a fingerprintStore.
     *
     * @param fingerprintStoreDTO the entity to save.
     * @return the persisted entity.
     */
    public FingerprintStoreDTO save(FingerprintStoreDTO fingerprintStoreDTO) {
        LOG.debug("Request to save FingerprintStore : {}", fingerprintStoreDTO);
        FingerprintStore fingerprintStore = fingerprintStoreMapper.toEntity(fingerprintStoreDTO);
        fingerprintStore = fingerprintStoreRepository.save(fingerprintStore);
        return fingerprintStoreMapper.toDto(fingerprintStore);
    }

    /**
     * Update a fingerprintStore.
     *
     * @param fingerprintStoreDTO the entity to save.
     * @return the persisted entity.
     */
    public FingerprintStoreDTO update(FingerprintStoreDTO fingerprintStoreDTO) {
        LOG.debug("Request to update FingerprintStore : {}", fingerprintStoreDTO);
        FingerprintStore fingerprintStore = fingerprintStoreMapper.toEntity(fingerprintStoreDTO);
        fingerprintStore = fingerprintStoreRepository.save(fingerprintStore);
        return fingerprintStoreMapper.toDto(fingerprintStore);
    }

    /**
     * Partially update a fingerprintStore.
     *
     * @param fingerprintStoreDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FingerprintStoreDTO> partialUpdate(FingerprintStoreDTO fingerprintStoreDTO) {
        LOG.debug("Request to partially update FingerprintStore : {}", fingerprintStoreDTO);

        return fingerprintStoreRepository
            .findById(fingerprintStoreDTO.getId())
            .map(existingFingerprintStore -> {
                fingerprintStoreMapper.partialUpdate(existingFingerprintStore, fingerprintStoreDTO);

                return existingFingerprintStore;
            })
            .map(fingerprintStoreRepository::save)
            .map(fingerprintStoreMapper::toDto);
    }

    /**
     * Get all the fingerprintStores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<FingerprintStoreDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FingerprintStores");
        return fingerprintStoreRepository.findAll(pageable).map(fingerprintStoreMapper::toDto);
    }

    /**
     * Get one fingerprintStore by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<FingerprintStoreDTO> findOne(String id) {
        LOG.debug("Request to get FingerprintStore : {}", id);
        return fingerprintStoreRepository.findById(id).map(fingerprintStoreMapper::toDto);
    }

    /**
     * Delete the fingerprintStore by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete FingerprintStore : {}", id);
        fingerprintStoreRepository.deleteById(id);
    }

    public Page<FingerprintStore> getByTypePerson(Pageable pageable) {
        return fingerprintStoreRepository.findByType(ActorType.PERSON, pageable);
    }
}
