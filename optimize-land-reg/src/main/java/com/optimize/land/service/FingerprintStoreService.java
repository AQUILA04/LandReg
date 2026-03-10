package com.optimize.land.service;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.repository.FingerprintStoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class FingerprintStoreService extends GenericService<FingerprintStore, Long> {

    protected FingerprintStoreService(FingerprintStoreRepository repository) {
        super(repository);
    }

    public Set<FingerprintStore> getByRid(String rid) {
        Set<FingerprintStore> fingerprintStores = ((FingerprintStoreRepository) repository).findByRid(rid);
        fingerprintStores.forEach(finger -> finger.setFingerprintImage(null));
        return fingerprintStores;
    }

    @Override
    public FingerprintStoreRepository getRepository() {
        return (FingerprintStoreRepository) super.getRepository();
    }
}
