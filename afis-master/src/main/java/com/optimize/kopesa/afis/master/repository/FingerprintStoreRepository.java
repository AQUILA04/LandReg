package com.optimize.kopesa.afis.master.repository;

import com.optimize.kopesa.afis.master.domain.FingerprintStore;
import com.optimize.kopesa.afis.master.domain.enumeration.ActorType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the FingerprintStore entity.
 */
@Repository
public interface FingerprintStoreRepository extends MongoRepository<FingerprintStore, String> {

    List<FingerprintStore> findByRid(String rid);

    int countByType(ActorType type);
}
