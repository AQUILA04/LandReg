package com.lesadrax.registrationclient.data.model.dto;

public class PrivateLegalEntityRequest {

    private Long id;
    private String uin;
    private String companyName;
    private String address;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String email;
    private String entityType;
    private IdentificationDocRequest identificationDoc;
    private String mainActivity;
    private String acronym;
    private String companyCreatedDate;
    private String representativeUIN;
    private String representativeFullname;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
        this.secondaryPhoneNumber = secondaryPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public IdentificationDocRequest getIdentificationDoc() {
        return identificationDoc;
    }

    public void setIdentificationDoc(IdentificationDocRequest identificationDoc) {
        this.identificationDoc = identificationDoc;
    }

    public String getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getCompanyCreatedDate() {
        return companyCreatedDate;
    }

    public void setCompanyCreatedDate(String companyCreatedDate) {
        this.companyCreatedDate = companyCreatedDate;
    }

    public String getRepresentativeUIN() {
        return representativeUIN;
    }

    public void setRepresentativeUIN(String representativeUIN) {
        this.representativeUIN = representativeUIN;
    }

    public String getRepresentativeFullname() {
        return representativeFullname;
    }

    public void setRepresentativeFullname(String representativeFullname) {
        this.representativeFullname = representativeFullname;
    }
}
