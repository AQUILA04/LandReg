package com.optimize.land.service;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.common.entities.service.GenericService;
import com.optimize.land.model.entity.Person;
import com.optimize.land.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonService extends GenericService<Person, Long> {

    protected PersonService(PersonRepository repository) {
        super(repository);
    }
}
