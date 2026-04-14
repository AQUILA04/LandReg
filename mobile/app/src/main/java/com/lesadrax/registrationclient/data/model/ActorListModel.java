package com.lesadrax.registrationclient.data.model;

public class ActorListModel {
    private int id;
    private String physicalPerson;
    private String informalGroup;
    private String privateLegalEntity;
    private String publicLegalEntity;
    private String uin;

    private String name;
    private String registrationStatus;
    private String statusObservation;
    private String rid;
    private String synchroBatchNumber;
    private String synchroPacketNumber;
    private String role;
    private String type;

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhysicalPerson() {
        return physicalPerson;
    }

    public void setPhysicalPerson(String physicalPerson) {
        this.physicalPerson = physicalPerson;
    }

    public String getInformalGroup() {
        return informalGroup;
    }

    public void setInformalGroup(String informalGroup) {
        this.informalGroup = informalGroup;
    }

    public String getPrivateLegalEntity() {
        return privateLegalEntity;
    }

    public void setPrivateLegalEntity(String privateLegalEntity) {
        this.privateLegalEntity = privateLegalEntity;
    }

    public String getPublicLegalEntity() {
        return publicLegalEntity;
    }

    public void setPublicLegalEntity(String publicLegalEntity) {
        this.publicLegalEntity = publicLegalEntity;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getStatusObservation() {
        return statusObservation;
    }

    public void setStatusObservation(String statusObservation) {
        this.statusObservation = statusObservation;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
