package com.optimize.kopesa.afis.service.service.dto;

public class MatcherResponseDTO  {
    private String rid;
    private int batchId;
    private Double highestScore;
    private Boolean isFoundMatch;
    private String matchRID;

    public MatcherResponseDTO() {
    }

    public MatcherResponseDTO(String rid, int batchId, Double highestScore, Boolean isFoundMatch, String matchRID) {
        this.rid = rid;
        this.batchId = batchId;
        this.highestScore = highestScore;
        this.isFoundMatch = isFoundMatch;
        this.matchRID = matchRID;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public Boolean getFoundMatch() {
        return isFoundMatch;
    }

    public void setFoundMatch(Boolean foundMatch) {
        isFoundMatch = foundMatch;
    }

    public String getMatchRID() {
        return matchRID;
    }

    public void setMatchRID(String matchRID) {
        this.matchRID = matchRID;
    }

    @Override
    public String toString() {
        return "MatcherResponseDTO{" +
            "rid='" + rid + '\'' +
            ", batchId=" + batchId +
            ", highestScore=" + highestScore +
            ", isFoundMatch=" + isFoundMatch +
            ", matchRID='" + matchRID + '\'' +
            '}';
    }
}
