package com.lesadrax.registrationclient.data.model.dto;

import java.util.LinkedHashSet;
import java.util.Set;

public class ActorRegistrationRequest {

    private Long id;
    private PersonRequest physicalPerson;
    private InformalGroupRequest informalGroup;
    private PrivateLegalEntityRequest privateLegalEntity;
    private PublicLegalEntityRequest publicLegalEntity;
    private String uin;
    private String synchroBatchNumber;
    private String synchroPacketNumber;
    private String role;
    private String type;
    private Set<FingerprintStoreRequest> fingerprintStores = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonRequest getPhysicalPerson() {
        return physicalPerson;
    }

    public void setPhysicalPerson(PersonRequest physicalPerson) {
        this.physicalPerson = physicalPerson;
    }

    public InformalGroupRequest getInformalGroup() {
        return informalGroup;
    }

    public void setInformalGroup(InformalGroupRequest informalGroup) {
        this.informalGroup = informalGroup;
    }

    public PrivateLegalEntityRequest getPrivateLegalEntity() {
        return privateLegalEntity;
    }

    public void setPrivateLegalEntity(PrivateLegalEntityRequest privateLegalEntity) {
        this.privateLegalEntity = privateLegalEntity;
    }

    public PublicLegalEntityRequest getPublicLegalEntity() {
        return publicLegalEntity;
    }

    public void setPublicLegalEntity(PublicLegalEntityRequest publicLegalEntity) {
        this.publicLegalEntity = publicLegalEntity;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
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

    public Set<FingerprintStoreRequest> getFingerprintStores() {
        return fingerprintStores;
    }

    public void setFingerprintStores(Set<FingerprintStoreRequest> fingerprintStores) {
        this.fingerprintStores = fingerprintStores;
    }
}
