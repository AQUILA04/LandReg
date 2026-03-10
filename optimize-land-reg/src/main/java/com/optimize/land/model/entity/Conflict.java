package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.optimize.common.entities.entity.Auditable;
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
    private String conflictParty;
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
    private byte[] photoOfProof;
    private String procedureStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate settlementDate;
    private String settlementCompromiseNature;
    private String settlementActor;
    private String regulationWitnesses;
    private String finalDecisionProof;
    private byte[] settlementProofPhoto;
    private String rightRestrictionType;
    private String currentlyUseFor;
    private String agriculturalDevelopmentType;
    private String pointOfAttention;
    @JsonBackReference
    @OneToOne(mappedBy = "conflict")
    private Finding finding;
    private String modeAcquisition;
    private String siHeritageDeQui;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate siHeritageDateDeces;
    private Integer girlCount;
    private Integer boyCount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAcquisition;
    private String typePreuveAcquisition;
    private byte[] photoPreuveAcquisition;
    private byte[] photoTemoignage;
    private byte[] photoFicheTemoignage;




}
