package com.optimize.kopesa.afis.service.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A MatcherJobHistory.
 */
@Document(collection = "matcher_job_history")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MatcherJobHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("rid")
    private String rid;

    @Field("producer_count")
    private Integer producerCount;

    @Field("consumer_reponse_count")
    private Integer consumerReponseCount;

    @Field("high_score")
    private Double highScore;

    @Field("found_match")
    private Boolean foundMatch;

    @Field("matched_rid")
    private String matchedRID;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public MatcherJobHistory id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRid() {
        return this.rid;
    }

    public MatcherJobHistory rid(String rid) {
        this.setRid(rid);
        return this;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Integer getProducerCount() {
        return this.producerCount;
    }

    public MatcherJobHistory producerCount(Integer producerCount) {
        this.setProducerCount(producerCount);
        return this;
    }

    public void setProducerCount(Integer producerCount) {
        this.producerCount = producerCount;
    }

    public Integer getConsumerReponseCount() {
        return this.consumerReponseCount;
    }

    public MatcherJobHistory consumerReponseCount(Integer consumerReponseCount) {
        this.setConsumerReponseCount(consumerReponseCount);
        return this;
    }

    public void setConsumerReponseCount(Integer consumerReponseCount) {
        this.consumerReponseCount = consumerReponseCount;
    }

    public Double getHighScore() {
        return this.highScore;
    }

    public MatcherJobHistory highScore(Double highScore) {
        this.setHighScore(highScore);
        return this;
    }

    public void setHighScore(Double highScore) {
        this.highScore = highScore;
    }

    public Boolean getFoundMatch() {
        return this.foundMatch;
    }

    public MatcherJobHistory foundMatch(Boolean foundMatch) {
        this.setFoundMatch(foundMatch);
        return this;
    }

    public void setFoundMatch(Boolean foundMatch) {
        this.foundMatch = foundMatch;
    }

    public String getMatchedRID() {
        return this.matchedRID;
    }

    public MatcherJobHistory matchedRID(String matchedRID) {
        this.setMatchedRID(matchedRID);
        return this;
    }

    public void setMatchedRID(String matchedRID) {
        this.matchedRID = matchedRID;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MatcherJobHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((MatcherJobHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MatcherJobHistory{" +
            "id=" + getId() +
            ", rid='" + getRid() + "'" +
            ", producerCount=" + getProducerCount() +
            ", consumerReponseCount=" + getConsumerReponseCount() +
            ", highScore=" + getHighScore() +
            ", foundMatch='" + getFoundMatch() + "'" +
            ", matchedRID='" + getMatchedRID() + "'" +
            "}";
    }
}
