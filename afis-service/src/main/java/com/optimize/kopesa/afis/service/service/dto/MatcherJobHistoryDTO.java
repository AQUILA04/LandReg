package com.optimize.kopesa.afis.service.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.optimize.kopesa.afis.service.domain.MatcherJobHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MatcherJobHistoryDTO implements Serializable {

    private String id;

    private String rid;

    private Integer producerCount;

    private Integer consumerReponseCount;

    private Double highScore;

    private Boolean foundMatch;

    private String matchedRID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Integer getProducerCount() {
        return producerCount;
    }

    public void setProducerCount(Integer producerCount) {
        this.producerCount = producerCount;
    }

    public Integer getConsumerReponseCount() {
        return consumerReponseCount;
    }

    public void setConsumerReponseCount(Integer consumerReponseCount) {
        this.consumerReponseCount = consumerReponseCount;
    }

    public Double getHighScore() {
        return highScore;
    }

    public void setHighScore(Double highScore) {
        this.highScore = highScore;
    }

    public Boolean getFoundMatch() {
        return foundMatch;
    }

    public void setFoundMatch(Boolean foundMatch) {
        this.foundMatch = foundMatch;
    }

    public String getMatchedRID() {
        return matchedRID;
    }

    public void setMatchedRID(String matchedRID) {
        this.matchedRID = matchedRID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MatcherJobHistoryDTO)) {
            return false;
        }

        MatcherJobHistoryDTO matcherJobHistoryDTO = (MatcherJobHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, matcherJobHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MatcherJobHistoryDTO{" +
            "id='" + getId() + "'" +
            ", rid='" + getRid() + "'" +
            ", producerCount=" + getProducerCount() +
            ", consumerReponseCount=" + getConsumerReponseCount() +
            ", highScore=" + getHighScore() +
            ", foundMatch='" + getFoundMatch() + "'" +
            ", matchedRID='" + getMatchedRID() + "'" +
            "}";
    }
}
