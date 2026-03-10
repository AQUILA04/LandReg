package com.optimize.kopesa.afis.master.service.dto;

public class RegistrationProcessorFeedback {
    private String rid;
    private Boolean isFoundMatch;
    private String matchedRID;

    public RegistrationProcessorFeedback() {
    }

    public RegistrationProcessorFeedback(String rid, Boolean isFoundMatch, String matchedRID) {
        this.rid = rid;
        this.isFoundMatch = isFoundMatch;
        this.matchedRID = matchedRID;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Boolean getFoundMatch() {
        return isFoundMatch;
    }

    public void setFoundMatch(Boolean foundMatch) {
        isFoundMatch = foundMatch;
    }

    public String getMatchedRID() {
        return matchedRID;
    }

    public void setMatchedRID(String matchedRID) {
        this.matchedRID = matchedRID;
    }
}
