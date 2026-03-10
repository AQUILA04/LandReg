package com.optimize.land.service;

import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.PublicLegalEntity;
import com.optimize.land.repository.PublicLegalEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PublicLegalEntityService extends GenericService<PublicLegalEntity, Long> {

    protected PublicLegalEntityService(PublicLegalEntityRepository repository) {
        super(repository);
    }
}
