package com.optimize.land.service;

import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.CheckListOperation;
import com.optimize.land.repository.CheckListOperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CheckListOperationService extends GenericService<CheckListOperation, Long> {

    protected CheckListOperationService(CheckListOperationRepository repository) {
        super(repository);
    }
}
