package com.lesadrax.registrationclient.data.model.dto;

public class InformalGroupRequest {

    private Long id;
    private String uin;
    private String groupName;
    private String address;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String email;
    private String groupType;
    private String representativeUIN;
    private String representativeFullname;
    private String secondaryRepresentativeUIN;
    private String secondaryRepresentativeFullname;
    private String thirdRepresentativeUIN;
    private String thirdRepresentativeFullname;
    private String mandatePhoto;
    private String mandatePhotoContentType;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
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

    public String getSecondaryRepresentativeUIN() {
        return secondaryRepresentativeUIN;
    }

    public void setSecondaryRepresentativeUIN(String secondaryRepresentativeUIN) {
        this.secondaryRepresentativeUIN = secondaryRepresentativeUIN;
    }

    public String getSecondaryRepresentativeFullname() {
        return secondaryRepresentativeFullname;
    }

    public void setSecondaryRepresentativeFullname(String secondaryRepresentativeFullname) {
        this.secondaryRepresentativeFullname = secondaryRepresentativeFullname;
    }

    public String getThirdRepresentativeUIN() {
        return thirdRepresentativeUIN;
    }

    public void setThirdRepresentativeUIN(String thirdRepresentativeUIN) {
        this.thirdRepresentativeUIN = thirdRepresentativeUIN;
    }

    public String getThirdRepresentativeFullname() {
        return thirdRepresentativeFullname;
    }

    public void setThirdRepresentativeFullname(String thirdRepresentativeFullname) {
        this.thirdRepresentativeFullname = thirdRepresentativeFullname;
    }

    public String getMandatePhoto() {
        return mandatePhoto;
    }

    public void setMandatePhoto(String mandatePhoto) {
        this.mandatePhoto = mandatePhoto;
    }

    public String getMandatePhotoContentType() {
        return mandatePhotoContentType;
    }

    public void setMandatePhotoContentType(String mandatePhotoContentType) {
        this.mandatePhotoContentType = mandatePhotoContentType;
    }
}
