package com.optimize.land.service;

import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.PrivateLegalEntity;
import com.optimize.land.repository.PrivateLegalEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PrivateLegalEntityService extends GenericService<PrivateLegalEntity, Long> {

    protected PrivateLegalEntityService(PrivateLegalEntityRepository repository) {
        super(repository);
    }
}
