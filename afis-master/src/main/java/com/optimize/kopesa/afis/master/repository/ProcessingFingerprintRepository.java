package com.optimize.kopesa.afis.master.repository;

import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the ProcessingFingerprint entity.
 */
@Repository
public interface ProcessingFingerprintRepository extends MongoRepository<ProcessingFingerprint, String> {
    List<ProcessingFingerprint> findByRid(String rid);
}
