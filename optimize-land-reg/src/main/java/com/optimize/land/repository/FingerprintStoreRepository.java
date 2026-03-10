package com.optimize.land.repository;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.FingerprintStore;

import java.util.Set;

public interface FingerprintStoreRepository extends GenericRepository<FingerprintStore, Long> {

    Set<FingerprintStore> findByRid(String rid);
}
