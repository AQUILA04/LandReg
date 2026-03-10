package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.jms.model.RegistrationProcessorFeedback;
import com.optimize.land.model.enumeration.MatchingHistoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FingerprintMatchingHistory extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rid;
    private Boolean foundMatch;
    private String matchedRID;
    @Enumerated(EnumType.STRING)
    private MatchingHistoryStatus status = MatchingHistoryStatus.SENT;

    public FingerprintMatchingHistory(String rid) {
        this.rid = rid;
        this.status = MatchingHistoryStatus.SENT;
    }

    public FingerprintMatchingHistory addFeedback(RegistrationProcessorFeedback feedback) {
        this.foundMatch = feedback.getFoundMatch();
        this.matchedRID = feedback.getMatchedRID();
        this.status = MatchingHistoryStatus.RECEIVED;
        return this;
    }
}
