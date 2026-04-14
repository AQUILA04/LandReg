package com.lesadrax.registrationclient.data.model;

import com.google.gson.annotations.SerializedName;

// Classe Conflict
import com.google.gson.annotations.SerializedName;

// Classe Conflict
public class Conflict {

    @SerializedName("id")
    private int id;

    @SerializedName("conflictParty")
    private String conflictParty;

    @SerializedName("firstConflictPartyNUP")
    private String firstConflictPartyNUP;

    @SerializedName("firstConflictPartyOccupationDurationInMonth")
    private String firstConflictPartyOccupationDurationInMonth;

    @SerializedName("secondConflictPartyNUP")
    private String secondConflictPartyNUP;

    @SerializedName("secondConflictPartyOccupationDurationInMonth")
    private String secondConflictPartyOccupationDurationInMonth;

    @SerializedName("conflictObject")
    private String conflictObject;

    @SerializedName("rightClaimed")
    private String rightClaimed;

    @SerializedName("rightClaimedOrigin")
    private String rightClaimedOrigin;

    @SerializedName("institutionInvolved")
    private String institutionInvolved;

    @SerializedName("seizureProof")
    private String seizureProof;

    @SerializedName("exhibitAndEvidence")
    private String exhibitAndEvidence;

    @SerializedName("photoOfProof")
    private String photoOfProof;

    @SerializedName("procedureStatus")
    private String procedureStatus;

    @SerializedName("settlementDate")
    private String settlementDate;

    @SerializedName("settlementCompromiseNature")
    private String settlementCompromiseNature;

    @SerializedName("settlementActor")
    private String settlementActor;

    @SerializedName("regulationWitnesses")
    private String regulationWitnesses;

    @SerializedName("finalDecisionProof")
    private String finalDecisionProof;

    @SerializedName("settlementProofPhoto")
    private String settlementProofPhoto;

    @SerializedName("rightRestrictionType")
    private String rightRestrictionType;

    @SerializedName("currentlyUseFor")
    private String currentlyUseFor;

    @SerializedName("agriculturalDevelopmentType")
    private String agriculturalDevelopmentType;

    @SerializedName("mainDevelopmentCrop")
    private String mainDevelopmentCrop;

    @SerializedName("yearOfFirstOccupation")
    private String yearOfFirstOccupation;

    @SerializedName("actualUserUIN")
    private String actualUserUIN;

    @SerializedName("landTopographyType")
    private String landTopographyType;

    @SerializedName("currentSettlementNature")
    private String currentSettlementNature;

    @SerializedName("precisions")
    private String precisions;

    @SerializedName("additionalInformation")
    private String additionalInformation;

    @SerializedName("modeAcquisition")
    private String modeAcquisition;

    @SerializedName("siHeritageDeQui")
    private String siHeritageDeQui;

    @SerializedName("siHeritageDateDeces")
    private String siHeritageDateDeces;

    @SerializedName("girlCount")
    private Integer girlCount;

    @SerializedName("boyCount")
    private Integer boyCount;

    @SerializedName("dateAcquisition")
    private String dateAcquisition;

    @SerializedName("typePreuveAcquisition")
    private String typePreuveAcquisition;

    @SerializedName("photoTemoignage")
    private String photoTemoignage;

    @SerializedName("photoFicheTemoignage")
    private String photoFicheTemoignage;

    @SerializedName("pointOfAttention")
    private String pointOfAttention;

    // Getters et Setters
    // Tous les getters et setters sont générés ici pour chaque attribut

    // Exemple d'un getter et setter :
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConflictParty() {
        return conflictParty;
    }

    public void setConflictParty(String conflictParty) {
        this.conflictParty = conflictParty;
    }

    public String getFirstConflictPartyNUP() {
        return firstConflictPartyNUP;
    }

    public void setFirstConflictPartyNUP(String firstConflictPartyNUP) {
        this.firstConflictPartyNUP = firstConflictPartyNUP;
    }

    public String getFirstConflictPartyOccupationDurationInMonth() {
        return firstConflictPartyOccupationDurationInMonth;
    }

    public void setFirstConflictPartyOccupationDurationInMonth(String firstConflictPartyOccupationDurationInMonth) {
        this.firstConflictPartyOccupationDurationInMonth = firstConflictPartyOccupationDurationInMonth;
    }

    public String getSecondConflictPartyNUP() {
        return secondConflictPartyNUP;
    }

    public void setSecondConflictPartyNUP(String secondConflictPartyNUP) {
        this.secondConflictPartyNUP = secondConflictPartyNUP;
    }

    public String getSecondConflictPartyOccupationDurationInMonth() {
        return secondConflictPartyOccupationDurationInMonth;
    }

    public void setSecondConflictPartyOccupationDurationInMonth(String secondConflictPartyOccupationDurationInMonth) {
        this.secondConflictPartyOccupationDurationInMonth = secondConflictPartyOccupationDurationInMonth;
    }

    public String getConflictObject() {
        return conflictObject;
    }

    public void setConflictObject(String conflictObject) {
        this.conflictObject = conflictObject;
    }

    public String getRightClaimed() {
        return rightClaimed;
    }

    public void setRightClaimed(String rightClaimed) {
        this.rightClaimed = rightClaimed;
    }

    public String getRightClaimedOrigin() {
        return rightClaimedOrigin;
    }

    public void setRightClaimedOrigin(String rightClaimedOrigin) {
        this.rightClaimedOrigin = rightClaimedOrigin;
    }

    public String getInstitutionInvolved() {
        return institutionInvolved;
    }

    public void setInstitutionInvolved(String institutionInvolved) {
        this.institutionInvolved = institutionInvolved;
    }

    public String getSeizureProof() {
        return seizureProof;
    }

    public void setSeizureProof(String seizureProof) {
        this.seizureProof = seizureProof;
    }

    public String getExhibitAndEvidence() {
        return exhibitAndEvidence;
    }

    public void setExhibitAndEvidence(String exhibitAndEvidence) {
        this.exhibitAndEvidence = exhibitAndEvidence;
    }

    public String getPhotoOfProof() {
        return photoOfProof;
    }

    public void setPhotoOfProof(String photoOfProof) {
        this.photoOfProof = photoOfProof;
    }

    public String getProcedureStatus() {
        return procedureStatus;
    }

    public void setProcedureStatus(String procedureStatus) {
        this.procedureStatus = procedureStatus;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettlementCompromiseNature() {
        return settlementCompromiseNature;
    }

    public void setSettlementCompromiseNature(String settlementCompromiseNature) {
        this.settlementCompromiseNature = settlementCompromiseNature;
    }

    public String getSettlementActor() {
        return settlementActor;
    }

    public void setSettlementActor(String settlementActor) {
        this.settlementActor = settlementActor;
    }

    public String getRegulationWitnesses() {
        return regulationWitnesses;
    }

    public void setRegulationWitnesses(String regulationWitnesses) {
        this.regulationWitnesses = regulationWitnesses;
    }

    public String getFinalDecisionProof() {
        return finalDecisionProof;
    }

    public void setFinalDecisionProof(String finalDecisionProof) {
        this.finalDecisionProof = finalDecisionProof;
    }

    public String getSettlementProofPhoto() {
        return settlementProofPhoto;
    }

    public void setSettlementProofPhoto(String settlementProofPhoto) {
        this.settlementProofPhoto = settlementProofPhoto;
    }

    public String getRightRestrictionType() {
        return rightRestrictionType;
    }

    public void setRightRestrictionType(String rightRestrictionType) {
        this.rightRestrictionType = rightRestrictionType;
    }

    public String getCurrentlyUseFor() {
        return currentlyUseFor;
    }

    public void setCurrentlyUseFor(String currentlyUseFor) {
        this.currentlyUseFor = currentlyUseFor;
    }

    public String getAgriculturalDevelopmentType() {
        return agriculturalDevelopmentType;
    }

    public void setAgriculturalDevelopmentType(String agriculturalDevelopmentType) {
        this.agriculturalDevelopmentType = agriculturalDevelopmentType;
    }

    public String getMainDevelopmentCrop() {
        return mainDevelopmentCrop;
    }

    public void setMainDevelopmentCrop(String mainDevelopmentCrop) {
        this.mainDevelopmentCrop = mainDevelopmentCrop;
    }

    public String getYearOfFirstOccupation() {
        return yearOfFirstOccupation;
    }

    public void setYearOfFirstOccupation(String yearOfFirstOccupation) {
        this.yearOfFirstOccupation = yearOfFirstOccupation;
    }

    public String getActualUserUIN() {
        return actualUserUIN;
    }

    public void setActualUserUIN(String actualUserUIN) {
        this.actualUserUIN = actualUserUIN;
    }

    public String getLandTopographyType() {
        return landTopographyType;
    }

    public void setLandTopographyType(String landTopographyType) {
        this.landTopographyType = landTopographyType;
    }

    public String getCurrentSettlementNature() {
        return currentSettlementNature;
    }

    public void setCurrentSettlementNature(String currentSettlementNature) {
        this.currentSettlementNature = currentSettlementNature;
    }

    public String getPrecisions() {
        return precisions;
    }

    public void setPrecisions(String precisions) {
        this.precisions = precisions;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getModeAcquisition() {
        return modeAcquisition;
    }

    public void setModeAcquisition(String modeAcquisition) {
        this.modeAcquisition = modeAcquisition;
    }

    public String getSiHeritageDeQui() {
        return siHeritageDeQui;
    }

    public void setSiHeritageDeQui(String siHeritageDeQui) {
        this.siHeritageDeQui = siHeritageDeQui;
    }

    public String getSiHeritageDateDeces() {
        return siHeritageDateDeces;
    }

    public void setSiHeritageDateDeces(String siHeritageDateDeces) {
        this.siHeritageDateDeces = siHeritageDateDeces;
    }

    public Integer getGirlCount() {
        return girlCount;
    }

    public void setGirlCount(Integer girlCount) {
        this.girlCount = girlCount;
    }

    public Integer getBoyCount() {
        return boyCount;
    }

    public void setBoyCount(Integer boyCount) {
        this.boyCount = boyCount;
    }

    public String getDateAcquisition() {
        return dateAcquisition;
    }

    public void setDateAcquisition(String dateAcquisition) {
        this.dateAcquisition = dateAcquisition;
    }

    public String getTypePreuveAcquisition() {
        return typePreuveAcquisition;
    }

    public void setTypePreuveAcquisition(String typePreuveAcquisition) {
        this.typePreuveAcquisition = typePreuveAcquisition;
    }

    public String getPhotoTemoignage() {
        return photoTemoignage;
    }

    public void setPhotoTemoignage(String photoTemoignage) {
        this.photoTemoignage = photoTemoignage;
    }

    public String getPhotoFicheTemoignage() {
        return photoFicheTemoignage;
    }

    public void setPhotoFicheTemoignage(String photoFicheTemoignage) {
        this.photoFicheTemoignage = photoFicheTemoignage;
    }

    public String getPointOfAttention() {
        return pointOfAttention;
    }

    public void setPointOfAttention(String pointOfAttention) {
        this.pointOfAttention = pointOfAttention;
    }
}

