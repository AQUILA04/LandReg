package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.Base64Image;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConflictDto {
    private Long id;
    private String conflictParty;
    private String firstConflictPartyNUP;
    private Integer firstConflictPartyOccupationDurationInMonth;
    private String secondConflictPartyNUP;
    private Integer secondConflictPartyOccupationDurationInMonth;
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
    @Base64Image
    private String photoOfProof;
    private String procedureStatus;
    private LocalDate settlementDate;
    private String settlementCompromiseNature;
    private String settlementActor;
    private String regulationWitnesses;
    private String finalDecisionProof;
    @Base64Image
    private String settlementProofPhoto;
    private String rightRestrictionType;
    private String currentlyUseFor;
    private String agriculturalDevelopmentType;
    private String pointOfAttention;
    private String modeAcquisition;
    private String siHeritageDeQui;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate siHeritageDateDeces;
    private Integer girlCount;
    private Integer boyCount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAcquisition;
    private String typePreuveAcquisition;
    @Base64Image
    private String photoPreuveAcquisition;
    @Base64Image
    private String photoTemoignage;
    @Base64Image
    private String photoFicheTemoignage;
}
