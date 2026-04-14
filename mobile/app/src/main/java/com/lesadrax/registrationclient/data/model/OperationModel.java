package com.lesadrax.registrationclient.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Classe principale
public class OperationModel {

    @SerializedName("id")
    private int id;

    @SerializedName("nup")
    private String nup;

    @SerializedName("region")
    private String region;

    @SerializedName("prefecture")
    private String prefecture;

    @SerializedName("commune")
    private String commune;

    @SerializedName("canton")
    private String canton;

    @SerializedName("locality")
    private String locality;

    @SerializedName("personType")
    private String personType;

    @SerializedName("superficie")
    private String superficie;

    @SerializedName("uin")
    private String uin;

    @SerializedName("hasConflict")
    private boolean hasConflict;

    @SerializedName("firstCheckListOperation")
    private Checklist firstCheckListOperation;

    @SerializedName("lastCheckListOperation")
    private Checklist lastCheckListOperation;

    @SerializedName("conflict")
    private Conflict conflict;

    // Getters et Setters
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

    public String getSuperficie() {
        return superficie;
    }

    public void setSuperficie(String superficie) {
        this.superficie = superficie;
    }
}
