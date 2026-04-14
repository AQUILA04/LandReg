package com.lesadrax.registrationclient.data.model;

import java.io.Serializable;

public class ActorModel implements Serializable {
    private String uin;
    private String name;
    private String firstname;
    private String lastname;
    private ActorType type;

    private String email;

    private String address;

    private String primaryPhone;

    private String identificationDocType;

    private String identificationDocNumber;

    private String role;

    private String contact;

    public ActorModel(String uin, String name, String firstname, String lastname, String s) {
        this.uin = uin;
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Getters et Setters
    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ActorType getType() {
        return type;
    }

    public void setType(ActorType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getIdentificationDocType() {
        return identificationDocType;
    }

    public void setIdentificationDocType(String identificationDocType) {
        this.identificationDocType = identificationDocType;
    }

    public String getIdentificationDocNumber() {
        return identificationDocNumber;
    }

    public void setIdentificationDocNumber(String identificationDocNumber) {
        this.identificationDocNumber = identificationDocNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "ActorModel{" +
                "uin='" + uin + '\'' +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", type=" + type +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", primaryPhone='" + primaryPhone + '\'' +
                ", identificationDocType='" + identificationDocType + '\'' +
                ", identificationDocNumber='" + identificationDocNumber + '\'' +
                '}';
    }
}
