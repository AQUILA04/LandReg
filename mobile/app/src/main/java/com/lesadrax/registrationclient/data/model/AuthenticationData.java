package com.lesadrax.registrationclient.data.model;

public class AuthenticationData {
    private String uin;
    private String fingerprint;
    private String role;

    // Constructeur
    public AuthenticationData(String uin, String fingerprint, String role) {
        this.uin = uin;
        this.fingerprint = fingerprint;
        this.role = role;
    }

    // Getters et Setters
    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AuthenticationData{" +
                "uin='" + uin + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

