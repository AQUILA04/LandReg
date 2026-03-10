package com.optimize.land.repository;

import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.model.projection.FindingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindingRepository extends GenericRepository<Finding, Long> {

    Page<FindingProjection> findByStateOrderByIdDesc(State state, Pageable pageable);
    Page<FindingProjection> findByStateAndOperatorAgentOrderByIdDesc(State state, String agent, Pageable pageable);
}
