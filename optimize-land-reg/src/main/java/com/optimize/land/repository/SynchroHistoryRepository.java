package com.optimize.land.repository;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.SynchroHistory;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SynchroHistoryRepository extends GenericRepository<SynchroHistory, Long> {
    Optional<SynchroHistory> findByBatchNumber(String batchNumber);

    @Query("""
            SELECT s
            FROM SynchroHistory s
            WHERE s.state = com.optimize.common.entities.enums.State.ENABLED
            AND (cast(:startDate as date) IS NULL OR s.createdDate >= :startDate)
            AND (cast(:endDate as date) IS NULL OR s.createdDate <= :endDate)
            ORDER BY s.id DESC
            """)
    Page<SynchroHistory> filterByDate(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);

    @Query("""
            SELECT s
            FROM SynchroHistory s
            WHERE s.state = com.optimize.common.entities.enums.State.ENABLED
            AND (cast(:startDate as date) IS NULL OR s.createdDate >= :startDate)
            AND (cast(:endDate as date) IS NULL OR s.createdDate <= :endDate)
            ORDER BY s.id DESC
            """)
    java.util.List<SynchroHistory> findAllByDate(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    boolean existsByBatchNumberAndPacketsNumberContains(String batchNumber, String packetNumber);

    default SynchroHistory getByBatchNumber(String batchNumber) {
        return findByBatchNumber(batchNumber).orElseThrow(() -> new RuntimeException(" Synchro history Not found with batch number " + batchNumber));
    }
}
