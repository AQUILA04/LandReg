package com.optimize.land.controller.export;

import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.util.Response;
import com.optimize.land.model.dto.export.ExportRequestDto;
import com.optimize.land.model.entity.export.ExportJob;
import com.optimize.land.service.export.ExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@RestController
@RequestMapping("land-reg/api/v1/exports")
@RequiredArgsConstructor
@Slf4j
public class ExportController {

    private final ExportService exportService;

    protected Response success(Object data, String message) {
        return Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .service("optimize-land-reg")
                .data(data)
                .build();
    }

    protected Response success(Object data, String message, HttpStatus status) {
        return Response.builder()
                .status(status)
                .statusCode(status.value())
                .message(message)
                .service("optimize-land-reg")
                .data(data)
                .build();
    }

    @PostMapping("/mass-export")
    public ResponseEntity<Response> initiateExport(@RequestBody @Valid ExportRequestDto request) {
        log.info("Initiating mass export request for module {}", request.getTargetModule());
        ExportJob job = exportService.initiateExport(request);
        exportService.processExport(job.getJobId(), request); // Async call
        return new ResponseEntity<>(success(Collections.singletonMap("jobId", job.getJobId()), "Export job initiated"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<Response> getExportStatus(@PathVariable String jobId) {
        log.info("Checking status for job {}", jobId);
        ExportJob job = exportService.getJobStatus(jobId);
        return new ResponseEntity<>(success(job, "Job status retrieved"), HttpStatus.OK);
    }

    @GetMapping("/download/{jobId}")
    public ResponseEntity<Resource> downloadExportFile(@PathVariable String jobId) {
        log.info("Downloading file for job {}", jobId);
        ExportJob job = exportService.getJobStatus(jobId);

        if (!"COMPLETED".equals(job.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            Path file = Paths.get(job.getFilePath());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = "application/octet-stream";
                if ("CSV".equalsIgnoreCase(job.getFileFormat())) {
                    contentType = "text/csv";
                } else if ("XLSX".equalsIgnoreCase(job.getFileFormat())) {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            log.error("Error formatting file path for download", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
