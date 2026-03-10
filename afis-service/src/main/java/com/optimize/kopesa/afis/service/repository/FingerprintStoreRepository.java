package com.optimize.kopesa.afis.service.repository;

import com.optimize.kopesa.afis.service.domain.FingerprintStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the FingerprintStore entity.
 */
@Repository
public interface FingerprintStoreRepository extends MongoRepository<FingerprintStore, String> {}
