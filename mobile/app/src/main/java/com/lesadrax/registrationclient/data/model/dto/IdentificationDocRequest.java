package com.lesadrax.registrationclient.data.model.dto;

public class IdentificationDocRequest {

    private Long id;
    private String identificationDocType;
    private String otherIdentificationDocType;
    private String identificationDocNumber;
    private String identificationDocPhoto;
    private String identificationDocPhotoContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificationDocType() {
        return identificationDocType;
    }

    public void setIdentificationDocType(String identificationDocType) {
        this.identificationDocType = identificationDocType;
    }

    public String getOtherIdentificationDocType() {
        return otherIdentificationDocType;
    }

    public void setOtherIdentificationDocType(String otherIdentificationDocType) {
        this.otherIdentificationDocType = otherIdentificationDocType;
    }

    public String getIdentificationDocNumber() {
        return identificationDocNumber;
    }

    public void setIdentificationDocNumber(String identificationDocNumber) {
        this.identificationDocNumber = identificationDocNumber;
    }

    public String getIdentificationDocPhoto() {
        return identificationDocPhoto;
    }

    public void setIdentificationDocPhoto(String identificationDocPhoto) {
        this.identificationDocPhoto = identificationDocPhoto;
    }

    public String getIdentificationDocPhotoContentType() {
        return identificationDocPhotoContentType;
    }

    public void setIdentificationDocPhotoContentType(String identificationDocPhotoContentType) {
        this.identificationDocPhotoContentType = identificationDocPhotoContentType;
    }
}
