package com.optimize.land.service;

import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.Conflict;
import com.optimize.land.repository.ConflictRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConflictService extends GenericService<Conflict, Long> {

    protected ConflictService(ConflictRepository repository) {
        super(repository);
    }
}
