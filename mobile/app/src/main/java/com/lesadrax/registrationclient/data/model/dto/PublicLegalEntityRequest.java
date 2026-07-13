package com.lesadrax.registrationclient.data.model.dto;

public class PublicLegalEntityRequest {

    private Long id;
    private String uin;
    private String publicEntityType;
    private String phoneNumber;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getPublicEntityType() {
        return publicEntityType;
    }

    public void setPublicEntityType(String publicEntityType) {
        this.publicEntityType = publicEntityType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
