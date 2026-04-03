package com.optimize.land.repository;

import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.model.projection.FindingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FindingRepository extends GenericRepository<Finding, Long> {

    Page<FindingProjection> findByStateOrderByIdDesc(State state, Pageable pageable);
    Page<FindingProjection> findByStateAndOperatorAgentOrderByIdDesc(State state, String agent, Pageable pageable);

    @Query("""
            SELECT f
            FROM Finding f
            WHERE (LOWER(f.nup) LIKE :keyword OR
                   LOWER(f.region) LIKE :keyword OR
                   LOWER(f.prefecture) LIKE :keyword OR
                   LOWER(f.commune) LIKE :keyword OR
                   LOWER(f.canton) LIKE :keyword OR
                   LOWER(f.locality) LIKE :keyword OR
                   LOWER(f.uin) LIKE :keyword OR
                   LOWER(f.surface) LIKE :keyword OR
                   LOWER(f.landForm) LIKE :keyword)
            AND f.state = com.optimize.common.entities.enums.State.ENABLED
            ORDER BY f.id DESC
            """)
    Page<FindingProjection> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
