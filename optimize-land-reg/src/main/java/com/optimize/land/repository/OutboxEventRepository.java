package com.optimize.land.repository;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.OutboxEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends GenericRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(String status);
}
