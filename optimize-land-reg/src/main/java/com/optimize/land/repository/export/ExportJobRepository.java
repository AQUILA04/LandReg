package com.optimize.land.repository.export;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.export.ExportJob;

import java.util.Optional;

public interface ExportJobRepository extends GenericRepository<ExportJob, Long> {
    Optional<ExportJob> findByJobId(String jobId);
}
