package com.optimize.kopesa.afis.master.service.dto;

public class BioAuthDto {
    private String uin;
    private String fingerprint;
    private String rid;
    private String role;

    public BioAuthDto() {
    }

    public BioAuthDto(String uin, String fingerprint, String rid, String role) {
        this.uin = uin;
        this.fingerprint = fingerprint;
        this.rid = rid;
        this.role = role;
    }

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

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "BioAuthDto{" +
            "uin='" + uin + '\'' +
            ", rid='" + rid + '\'' +
            ", role='" + role + '\'' +
            '}';
    }
}
