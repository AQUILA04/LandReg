package com.optimize.kopesa.afis.service.service.dto;

import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimize.kopesa.afis.service.domain.enumeration.Finger;
import com.optimize.kopesa.afis.service.domain.enumeration.HandType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.optimize.kopesa.afis.service.domain.FingerprintStore} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FingerprintStoreDTO implements Serializable {

    private String id;

    @NotNull
    private String rid;

    private HandType handType;

    private Finger fingerName;

    private byte[] fingerprintImage;

    private String fingerprintImageContentType;

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

    public HandType getHandType() {
        return handType;
    }

    public void setHandType(HandType handType) {
        this.handType = handType;
    }

    public Finger getFingerName() {
        return fingerName;
    }

    public void setFingerName(Finger fingerName) {
        this.fingerName = fingerName;
    }

    public byte[] getFingerprintImage() {
        return fingerprintImage;
    }

    public void setFingerprintImage(byte[] fingerprintImage) {
        this.fingerprintImage = fingerprintImage;
    }

    public String getFingerprintImageContentType() {
        return fingerprintImageContentType;
    }

    public void setFingerprintImageContentType(String fingerprintImageContentType) {
        this.fingerprintImageContentType = fingerprintImageContentType;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FingerprintStoreDTO fingerprintStoreDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fingerprintStoreDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FingerprintStoreDTO{" +
            "id='" + getId() + "'" +
            ", rid='" + getRid() + "'" +
            ", handType='" + getHandType() + "'" +
            ", fingerName='" + getFingerName() + "'" +
            ", fingerprintImage='" + getFingerprintImage() + "'" +
            "}";
    }

    public FingerprintTemplate getTemplate () {
        return new FingerprintTemplate(fingerprintImage);
    }

    // jhipster-needle-add-method
}
