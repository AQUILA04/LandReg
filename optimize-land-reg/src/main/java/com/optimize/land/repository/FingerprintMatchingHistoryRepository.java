package com.optimize.land.repository;

import com.optimize.common.entities.exception.ResourceNotFoundException;
import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.FingerprintMatchingHistory;
import com.optimize.land.model.enumeration.MatchingHistoryStatus;

import java.util.Optional;

public interface FingerprintMatchingHistoryRepository extends GenericRepository<FingerprintMatchingHistory, Long> {
    Optional<FingerprintMatchingHistory> findByRidAndStatus(String rid, MatchingHistoryStatus status);

    default FingerprintMatchingHistory getByRid(String rid) {
        return findByRidAndStatus(rid, MatchingHistoryStatus.SENT).orElseThrow(() -> new ResourceNotFoundException("FingerprintMatchingHistory not found for rid " + rid));
    }
}
