package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.Auditable;
import com.optimize.land.model.enumeration.ConflictParty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Conflict extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ConflictParty conflictParty;
    private String firstConflictPartyNUP;
    private String firstConflictPartyOccupationDurationInMonth;
    private String secondConflictPartyNUP;
    private String secondConflictPartyOccupationDurationInMonth;
    private String conflictObject;
    private String rightClaimed;
    private String rightClaimedOrigin;
    /**
     * Institution saisie
     */
    private String institutionInvolved;
    /**
     * Preuve de la saisine
     */
    private String seizureProof;
    private String exhibitAndEvidence;
    @Lob
    private byte[] photoOfProof;
    private String procedureStatus;
    private LocalDate settlementDate;
    private String settlementCompromiseNature;
    private String settlementActor;
    private String regulationWitnesses;
    private String finalDecisionProof;
    @Lob
    private byte[] settlementProofPhoto;
    private String rightRestrictionType;
    private String currentlyUseFor;
    private String agriculturalDevelopmentType;
    private String pointOfAttention;




}
