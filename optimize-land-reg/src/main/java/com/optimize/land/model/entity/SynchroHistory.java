package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.model.enumeration.SynchroStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A SynchroHistory.
 */
@Entity
@Table(name = "synchro_history")
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SynchroHistory extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "batch_number", unique = true)
    private String batchNumber;

    @NotNull
    @Column(name = "total_offline_count", nullable = false)
    private Integer totalOfflineCount;

    @NotNull
    @Column(name = "synchro_candidate_count", nullable = false)
    private Integer synchroCandidateCount;

    @Column(name = "total_received_count")
    private Integer totalReceivedCount;

    @Column(name = "success_packet_count")
    private Integer successPacketCount;

    @Column(name = "failed_packet_count")
    private Integer failedPacketCount;

    @Column(name = "duplicated_packet_count")
    private Integer duplicatedPacketCount;

    @Column(name = "pending_packet_count")
    private Integer pendingPacketCount;

    @Column(name = "packets_number")
    private String packetsNumber;

    @Column(name = "init_date")
    private LocalDate initDate;

    @Column(name = "first_packet_date")
    private LocalDate firstPacketDate;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @Column(name = "operator_agent")
    private String operatorAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "synchro_status")
    private SynchroStatus synchroStatus;

    @Column(name = "last_packet_date")
    private ZonedDateTime lastPacketDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SynchroHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((SynchroHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SynchroHistory{" +
            "id=" + getId() +
            ", batchNumber='" + getBatchNumber() + "'" +
            ", totalOfflineCount=" + getTotalOfflineCount() +
            ", synchroCandidateCount=" + getSynchroCandidateCount() +
            ", totalReceivedCount=" + getTotalReceivedCount() +
            ", successPacketCount=" + getSuccessPacketCount() +
            ", failedPacketCount=" + getFailedPacketCount() +
            ", duplicatedPacketCount=" + getDuplicatedPacketCount() +
            ", pendingPacketCount=" + getPendingPacketCount() +
            ", packetsNumber='" + getPacketsNumber() + "'" +
            ", initDate='" + getInitDate() + "'" +
            ", firstPacketDate='" + getFirstPacketDate() + "'" +
            ", isFinished='" + getIsFinished() + "'" +
            ", operatorAgent='" + getOperatorAgent() + "'" +
            ", synchroStatus='" + getSynchroStatus() + "'" +
            ", lastPacketDate='" + getLastPacketDate() + "'" +
            "}";
    }


    public void incrementReceived () {
        if (Objects.isNull(this.totalReceivedCount) || this.totalReceivedCount == 0) {
            this.totalReceivedCount = 1;
            this.firstPacketDate = LocalDate.now();
            this.pendingPacketCount = 1;
            this.synchroStatus = SynchroStatus.UPLOADING;
        } else {
            this.totalReceivedCount += 1;
            this.pendingPacketCount += 1;
        }

        if (this.synchroCandidateCount.equals(totalReceivedCount)) {
            this.isFinished = Boolean.TRUE;
            this.synchroStatus = SynchroStatus.FINISHED;
        }
        this.lastPacketDate = ZonedDateTime.now();
    }

    public void incrementDuplicated () {
        if (Objects.isNull(this.duplicatedPacketCount) || this.duplicatedPacketCount == 0) {
            this.duplicatedPacketCount = 1;
        } else {
            this.duplicatedPacketCount += 1;
        }
        this.pendingPacketCount = totalReceivedCount - (duplicatedPacketCount + failedPacketCount + successPacketCount);
    }

    public void incrementFailed () {
        if (Objects.isNull(this.failedPacketCount) || this.failedPacketCount == 0) {
            this.failedPacketCount = 1;
        } else {
            this.failedPacketCount += 1;
        }
        this.pendingPacketCount = totalReceivedCount - (duplicatedPacketCount + failedPacketCount + successPacketCount);
    }

    public void incrementSuccess () {
        if (Objects.isNull(this.successPacketCount) || this.successPacketCount == 0) {
            this.successPacketCount = 1;
        } else {
            this.successPacketCount += 1;
        }
        this.pendingPacketCount = totalReceivedCount - (duplicatedPacketCount + failedPacketCount + successPacketCount);
    }

    public void addPacketNumber(String packetNumber) {
        if (StringUtils.isEmpty(this.packetsNumber)) {
            this.packetsNumber = packetNumber;
        } else {
            this.packetsNumber = this.packetsNumber + "|" + packetNumber;
        }
    }
}
