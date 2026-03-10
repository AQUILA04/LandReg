package com.optimize.land.service;

import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.Bordering;
import com.optimize.land.repository.BorderingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BorderingService extends GenericService<Bordering, Long> {

    protected BorderingService(BorderingRepository repository) {
        super(repository);
    }
}
