package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.land.model.enumeration.SynchroStatus;
import com.optimize.land.model.enumeration.SynchroType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class SynchroHistoryDto {
    private Long id;
    private String batchNumber;
    @NotNull
    private Integer totalOfflineCount;
    @NotNull
    private Integer synchroCandidateCount;
    private Integer totalReceivedCount;
    private Integer successPacketCount;
    private Integer failedPacketCount;
    private Integer duplicatedPacketCount;
    private Integer pendingPacketCount;
    private String packetsNumber;
    private LocalDate initDate;
    private LocalDate firstPacketDate;
    private Boolean isFinished;
    private String operatorAgent;
    private SynchroStatus synchroStatus;
    private ZonedDateTime lastPacketDate;
    @Enumerated(EnumType.STRING)
    private SynchroType type;
}
