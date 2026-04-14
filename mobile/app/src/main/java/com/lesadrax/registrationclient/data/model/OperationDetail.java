package com.lesadrax.registrationclient.data.model;

import com.google.gson.annotations.SerializedName;

public class OperationDetail {

    private int id;
    private String nup;
    private String region;
    private String prefecture;
    private String commune;
    private String canton;
    private String locality;
    private String personType;
    private String uin;
    private boolean hasConflict;
    private Checklist firstCheckListOperation;
    private Checklist lastCheckListOperation;
    private Conflict conflict;
    private String synchroBatchNumber;
    private String synchroPacketNumber;
    private String operatorAgent;

    private String surface;

    private String landForm;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNup() {
        return nup;
    }

    public void setNup(String nup) {
        this.nup = nup;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public boolean isHasConflict() {
        return hasConflict;
    }

    public void setHasConflict(boolean hasConflict) {
        this.hasConflict = hasConflict;
    }

    public Checklist getFirstCheckListOperation() {
        return firstCheckListOperation;
    }

    public void setFirstCheckListOperation(Checklist firstCheckListOperation) {
        this.firstCheckListOperation = firstCheckListOperation;
    }

    public Checklist getLastCheckListOperation() {
        return lastCheckListOperation;
    }

    public void setLastCheckListOperation(Checklist lastCheckListOperation) {
        this.lastCheckListOperation = lastCheckListOperation;
    }

    public Conflict getConflict() {
        return conflict;
    }

    public void setConflict(Conflict conflict) {
        this.conflict = conflict;
    }

    public String getSynchroBatchNumber() {
        return synchroBatchNumber;
    }

    public void setSynchroBatchNumber(String synchroBatchNumber) {
        this.synchroBatchNumber = synchroBatchNumber;
    }

    public String getSynchroPacketNumber() {
        return synchroPacketNumber;
    }

    public void setSynchroPacketNumber(String synchroPacketNumber) {
        this.synchroPacketNumber = synchroPacketNumber;
    }

    public String getOperatorAgent() {
        return operatorAgent;
    }

    public void setOperatorAgent(String operatorAgent) {
        this.operatorAgent = operatorAgent;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getLandForm() {
        return landForm;
    }

    public void setLandForm(String landForm) {
        this.landForm = landForm;
    }
}
