package com.optimize.land.service.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.opencsv.CSVWriter;
import com.optimize.common.entities.exception.ResourceNotFoundException;
import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.model.dto.ActorRespDto;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.model.entity.SynchroHistory;
import com.optimize.land.model.entity.export.ExportJob;
import com.optimize.land.model.dto.export.ExportRequestDto;
import com.optimize.land.repository.ActorRepository;
import com.optimize.land.repository.FindingRepository;
import com.optimize.land.repository.SynchroHistoryRepository;
import com.optimize.land.repository.export.ExportJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ExportJobRepository exportJobRepository;
    private final ActorRepository actorRepository;
    private final FindingRepository findingRepository;
    private final SynchroHistoryRepository synchroHistoryRepository;
    private final UserService userService;
    private final String EXPORT_DIR = "exports";

    @Transactional
    public ExportJob initiateExport(ExportRequestDto request) {
        User user = userService.getCurrentUser();

        ExportJob job = new ExportJob();
        job.setJobId(UUID.randomUUID().toString());
        job.setStatus("PENDING");
        job.setFileFormat(request.getFormat());
        job.setTargetModule(request.getTargetModule());

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            job.setAppliedFilters(mapper.writeValueAsString(request.getFilters()));
        } catch (Exception e) {
            log.warn("Failed to serialize filters", e);
            job.setAppliedFilters(request.getFilters() != null ? request.getFilters().toString() : "None");
        }

        job = exportJobRepository.save(job);

        log.info("Export Job {} initiated by user {} for module {} in format {}",
                job.getJobId(), user.getUsername(), job.getTargetModule(), job.getFileFormat());

        return job;
    }

    @Async
    @Transactional
    public void processExport(String jobId, ExportRequestDto request) {
        log.info("Starting background export process for job {}", jobId);
        ExportJob job = exportJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        try {
            job.setStatus("PROCESSING");
            exportJobRepository.save(job);

            Path dirPath = Paths.get(EXPORT_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = String.format("%s_%s.%s", request.getTargetModule(), timestamp, request.getFormat().toLowerCase());
            File file = new File(EXPORT_DIR, fileName);

            LocalDateTime start = request.getFilters() != null && request.getFilters().getStartDate() != null ? request.getFilters().getStartDate().atStartOfDay() : null;
            LocalDateTime end = request.getFilters() != null && request.getFilters().getEndDate() != null ? request.getFilters().getEndDate().atTime(23, 59, 59) : null;

            if ("ACTORS".equalsIgnoreCase(request.getTargetModule())) {
                List<ActorRespDto> actors = actorRepository.findAllActorsByDate(start, end);
                if ("CSV".equalsIgnoreCase(request.getFormat())) {
                    exportActorsToCsv(actors, file);
                } else {
                    exportActorsToXlsx(actors, file);
                }
            } else if ("FINDINGS".equalsIgnoreCase(request.getTargetModule())) {
                String region = request.getFilters() != null ? request.getFilters().getRegion() : null;
                String prefecture = request.getFilters() != null ? request.getFilters().getPrefecture() : null;
                String commune = request.getFilters() != null ? request.getFilters().getCommune() : null;
                String canton = request.getFilters() != null ? request.getFilters().getCanton() : null;

                List<Finding> findings = findingRepository.findAllByCriteria(region, prefecture, commune, canton, start, end);
                if ("CSV".equalsIgnoreCase(request.getFormat())) {
                    exportFindingsToCsv(findings, file);
                } else {
                    exportFindingsToXlsx(findings, file);
                }
            } else if ("SYNCHRO".equalsIgnoreCase(request.getTargetModule())) {
                List<SynchroHistory> histories = synchroHistoryRepository.findAllByDate(start, end);
                if ("CSV".equalsIgnoreCase(request.getFormat())) {
                    exportSynchroToCsv(histories, file);
                } else {
                    exportSynchroToXlsx(histories, file);
                }
            } else {
                throw new IllegalArgumentException("Unknown target module");
            }

            job.setStatus("COMPLETED");
            job.setFilePath(file.getAbsolutePath());
            exportJobRepository.save(job);
            log.info("Export Job {} completed successfully", jobId);

        } catch (Exception e) {
            log.error("Export job failed", e);
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            exportJobRepository.save(job);
        }
    }

    // --- CSV Generators ---

    private void exportActorsToCsv(List<ActorRespDto> actors, File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"ID", "UIN", "Nom", "Type", "Role", "Statut"});
            for (ActorRespDto actor : actors) {
                writer.writeNext(new String[]{
                        String.valueOf(actor.id()),
                        actor.uin() != null ? actor.uin() : "",
                        actor.name() != null ? actor.name() : "",
                        actor.type() != null ? actor.type().name() : "",
                        actor.role() != null ? actor.role().name() : "",
                        actor.status() != null ? actor.status().name() : ""
                });
            }
        }
    }

    private void exportFindingsToCsv(List<Finding> findings, File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"ID", "NUP", "Région", "Préfecture", "Commune", "Canton", "Localité", "UIN Propriétaire", "Agent"});
            for (Finding finding : findings) {
                writer.writeNext(new String[]{
                        String.valueOf(finding.getId()),
                        finding.getNup() != null ? finding.getNup() : "",
                        finding.getRegion() != null ? finding.getRegion() : "",
                        finding.getPrefecture() != null ? finding.getPrefecture() : "",
                        finding.getCommune() != null ? finding.getCommune() : "",
                        finding.getCanton() != null ? finding.getCanton() : "",
                        finding.getLocality() != null ? finding.getLocality() : "",
                        finding.getUin() != null ? finding.getUin() : "",
                        finding.getOperatorAgent() != null ? finding.getOperatorAgent() : ""
                });
            }
        }
    }

    private void exportSynchroToCsv(List<SynchroHistory> histories, File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"ID", "Batch Number", "Init Date", "Statut", "Agent"});
            for (SynchroHistory history : histories) {
                writer.writeNext(new String[]{
                        String.valueOf(history.getId()),
                        history.getBatchNumber() != null ? history.getBatchNumber() : "",
                        history.getInitDate() != null ? history.getInitDate().toString() : "",
                        history.getSynchroStatus() != null ? history.getSynchroStatus().name() : "",
                        history.getOperatorAgent() != null ? history.getOperatorAgent() : ""
                });
            }
        }
    }

    // --- XLSX Generators ---

    private void exportActorsToXlsx(List<ActorRespDto> actors, File file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Acteurs");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "UIN", "Nom", "Type", "Role", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for (ActorRespDto actor : actors) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(actor.id() != null ? String.valueOf(actor.id()) : "");
                row.createCell(1).setCellValue(actor.uin() != null ? actor.uin() : "");
                row.createCell(2).setCellValue(actor.name() != null ? actor.name() : "");
                row.createCell(3).setCellValue(actor.type() != null ? actor.type().name() : "");
                row.createCell(4).setCellValue(actor.role() != null ? actor.role().name() : "");
                row.createCell(5).setCellValue(actor.status() != null ? actor.status().name() : "");
            }
            workbook.write(out);
        }
    }

    private void exportFindingsToXlsx(List<Finding> findings, File file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Constatations");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "NUP", "Région", "Préfecture", "Commune", "Canton", "Localité", "UIN Propriétaire", "Agent"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for (Finding finding : findings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(finding.getId() != null ? String.valueOf(finding.getId()) : "");
                row.createCell(1).setCellValue(finding.getNup() != null ? finding.getNup() : "");
                row.createCell(2).setCellValue(finding.getRegion() != null ? finding.getRegion() : "");
                row.createCell(3).setCellValue(finding.getPrefecture() != null ? finding.getPrefecture() : "");
                row.createCell(4).setCellValue(finding.getCommune() != null ? finding.getCommune() : "");
                row.createCell(5).setCellValue(finding.getCanton() != null ? finding.getCanton() : "");
                row.createCell(6).setCellValue(finding.getLocality() != null ? finding.getLocality() : "");
                row.createCell(7).setCellValue(finding.getUin() != null ? finding.getUin() : "");
                row.createCell(8).setCellValue(finding.getOperatorAgent() != null ? finding.getOperatorAgent() : "");
            }
            workbook.write(out);
        }
    }

    private void exportSynchroToXlsx(List<SynchroHistory> histories, File file) throws Exception {
         try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Synchro");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Batch Number", "Init Date", "Statut", "Agent"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for (SynchroHistory history : histories) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(history.getId() != null ? String.valueOf(history.getId()) : "");
                row.createCell(1).setCellValue(history.getBatchNumber() != null ? history.getBatchNumber() : "");
                row.createCell(2).setCellValue(history.getInitDate() != null ? history.getInitDate().toString() : "");
                row.createCell(3).setCellValue(history.getSynchroStatus() != null ? history.getSynchroStatus().name() : "");
                row.createCell(4).setCellValue(history.getOperatorAgent() != null ? history.getOperatorAgent() : "");
            }
            workbook.write(out);
        }
    }

    public ExportJob getJobStatus(String jobId) {
        return exportJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }
}
