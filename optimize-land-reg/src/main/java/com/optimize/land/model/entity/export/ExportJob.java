package com.optimize.land.model.entity.export;

import com.optimize.common.entities.entity.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "export_job")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ExportJob extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String jobId;

    @Column(nullable = false)
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED

    private String fileFormat; // CSV, XLSX

    private String targetModule; // ACTORS, FINDINGS, SYNCHRO

    @Column(columnDefinition = "TEXT")
    private String appliedFilters; // JSON representation of filters

    private String filePath;

    private String errorMessage;
}
