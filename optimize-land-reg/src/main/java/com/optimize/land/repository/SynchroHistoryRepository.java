package com.optimize.land.repository;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.SynchroHistory;

import java.util.Optional;

public interface SynchroHistoryRepository extends GenericRepository<SynchroHistory, Long> {
    Optional<SynchroHistory> findByBatchNumber(String batchNumber);

    boolean existsByBatchNumberAndPacketsNumberContains(String batchNumber, String packetNumber);

    default SynchroHistory getByBatchNumber(String batchNumber) {
        return findByBatchNumber(batchNumber).orElseThrow(() -> new RuntimeException(" Synchro history Not found with batch number " + batchNumber));
    }
}
