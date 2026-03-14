package com.optimize.land.repository;

import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.AbstractActor;
import com.optimize.land.model.enumeration.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface BaseActorRepository<A extends AbstractActor, I extends Serializable>
        extends GenericRepository<A, I> {

    @EntityGraph(attributePaths = {"physicalPerson", "informalGroup", "privateLegalEntity", "publicLegalEntity"})
    @Override
    Optional<A> findById(I i);

    boolean existsByUin(String uin);

    Page<A> findByRegistrationStatusAndStateOrderByIdDesc(RegistrationStatus status, State state, Pageable pageable);
}
