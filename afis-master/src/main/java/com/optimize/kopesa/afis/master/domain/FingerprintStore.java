package com.optimize.kopesa.afis.master.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimize.kopesa.afis.master.domain.enumeration.ActorType;
import com.optimize.kopesa.afis.master.domain.enumeration.Finger;
import com.optimize.kopesa.afis.master.domain.enumeration.HandType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A FingerprintStore.
 */
@Document(collection = "fingerprint_store")
@SuppressWarnings("common-java:DuplicatedBlocks")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FingerprintStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("rid")
    private String rid;

    @Field("hand_type")
    private HandType handType;

    @Field("finger_name")
    private Finger fingerName;

    @NotNull
    @Field("fingerprint_image")
    private byte[] fingerprintImage;

    @Field("type")
    private ActorType type = ActorType.PERSON;

    @Field("fingerprint_image_content_type")
    private String fingerprintImageContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public FingerprintStore id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRid() {
        return this.rid;
    }

    public FingerprintStore rid(String rid) {
        this.setRid(rid);
        return this;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public HandType getHandType() {
        return this.handType;
    }

    public FingerprintStore handType(HandType handType) {
        this.setHandType(handType);
        return this;
    }

    public void setHandType(HandType handType) {
        this.handType = handType;
    }

    public Finger getFingerName() {
        return this.fingerName;
    }

    public FingerprintStore fingerName(Finger fingerName) {
        this.setFingerName(fingerName);
        return this;
    }

    public void setFingerName(Finger fingerName) {
        this.fingerName = fingerName;
    }

    public byte[] getFingerprintImage() {
        return this.fingerprintImage;
    }

    public FingerprintStore fingerprintImage(byte[] fingerprintImage) {
        this.setFingerprintImage(fingerprintImage);
        return this;
    }

    public void validData() {
        if (Objects.isNull(this.fingerprintImage) || this.fingerprintImage.length == 0) {
            throw new RuntimeException("Invalid null fingerprint");
        }
    }

    public void setFingerprintImage(byte[] fingerprintImage) {
        this.fingerprintImage = fingerprintImage;
    }

    public String getFingerprintImageContentType() {
        return this.fingerprintImageContentType;
    }

    public FingerprintStore fingerprintImageContentType(String fingerprintImageContentType) {
        this.fingerprintImageContentType = fingerprintImageContentType;
        return this;
    }

    public void setFingerprintImageContentType(String fingerprintImageContentType) {
        this.fingerprintImageContentType = fingerprintImageContentType;
    }

    public ActorType getType() {
        return type;
    }

    public void setType(ActorType type) {
        this.type = type;
    }

    public FingerprintStore type(ActorType type) {
        this.type = type;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @JsonIgnore
    public FingerprintTemplate getTemplate () {
        return new FingerprintTemplate(fingerprintImage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FingerprintStore)) {
            return false;
        }
        return getId() != null && getId().equals(((FingerprintStore) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FingerprintStore{" +
            "id=" + getId() +
            ", rid='" + getRid() + "'" +
            ", handType='" + getHandType() + "'" +
            ", fingerName='" + getFingerName() + "'" +
            ", fingerprintImageContentType='" + getFingerprintImageContentType() + "'" +
            "}";
    }
}
