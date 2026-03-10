package com.optimize.land.service;

import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.InformalGroup;
import com.optimize.land.repository.InformalGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InformalGroupService extends GenericService<InformalGroup, Long> {

    protected InformalGroupService(InformalGroupRepository repository) {
        super(repository);
    }
}
