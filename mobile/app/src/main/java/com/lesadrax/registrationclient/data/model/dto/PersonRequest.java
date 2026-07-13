package com.lesadrax.registrationclient.data.model.dto;

public class PersonRequest {

    private Long id;
    private String lastname;
    private String firstname;
    private String sex;
    private String maritalStatus;
    private String birthDate;
    private String placeOfBirth;
    private String nationality;
    private String profession;
    private String otherProfession;
    private String address;
    private String primaryPhone;
    private String secondaryPhone;
    private String email;
    private Boolean hasHandicap;
    private String socioCulturalGroup;
    private String handicapType;
    private String otherHandicapType;
    private Boolean hasIDDoc;
    private String witnessUIN;
    private IdentificationDocRequest identificationDoc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getOtherProfession() {
        return otherProfession;
    }

    public void setOtherProfession(String otherProfession) {
        this.otherProfession = otherProfession;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHasHandicap() {
        return hasHandicap;
    }

    public void setHasHandicap(Boolean hasHandicap) {
        this.hasHandicap = hasHandicap;
    }

    public String getSocioCulturalGroup() {
        return socioCulturalGroup;
    }

    public void setSocioCulturalGroup(String socioCulturalGroup) {
        this.socioCulturalGroup = socioCulturalGroup;
    }

    public String getHandicapType() {
        return handicapType;
    }

    public void setHandicapType(String handicapType) {
        this.handicapType = handicapType;
    }

    public String getOtherHandicapType() {
        return otherHandicapType;
    }

    public void setOtherHandicapType(String otherHandicapType) {
        this.otherHandicapType = otherHandicapType;
    }

    public Boolean getHasIDDoc() {
        return hasIDDoc;
    }

    public void setHasIDDoc(Boolean hasIDDoc) {
        this.hasIDDoc = hasIDDoc;
    }

    public String getWitnessUIN() {
        return witnessUIN;
    }

    public void setWitnessUIN(String witnessUIN) {
        this.witnessUIN = witnessUIN;
    }

    public IdentificationDocRequest getIdentificationDoc() {
        return identificationDoc;
    }

    public void setIdentificationDoc(IdentificationDocRequest identificationDoc) {
        this.identificationDoc = identificationDoc;
    }
}
