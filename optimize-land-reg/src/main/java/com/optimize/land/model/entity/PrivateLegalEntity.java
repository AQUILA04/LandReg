package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.model.enumeration.PrivateEntityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A PrivateLegalEntity.
 */
@Entity
@Table(name = "private_legal_entity")
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PrivateLegalEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(min = 10, max = 15)
    @Column(name = "uin", length = 15)
    private String uin;

    @NotNull
    @Size(min = 3, max = 60)
    @Column(name = "company_name", length = 60, nullable = false, unique = true)
    private String companyName;

    @NotNull
    @Size(min = 4, max = 200)
    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @NotNull
    @Size(min = 8, max = 11)
    @Column(name = "phone_number", length = 11, nullable = false)
    private String phoneNumber;

    @Size(min = 8, max = 11)
    @Column(name = "secondary_phone_number", length = 11)
    private String secondaryPhoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private PrivateEntityType entityType;

    @Column(name = "identification_doc_type")
    private String identificationDocType;

    @NotNull
    @Column(name = "identification_doc_number", nullable = false)
    private String identificationDocNumber;

    @Lob
    @Column(name = "identification_doc_photo", nullable = false)
    private byte[] identificationDocPhoto;

    @NotNull
    @Column(name = "identification_doc_photo_content_type", nullable = false)
    private String identificationDocPhotoContentType;

    @NotNull
    @Column(name = "main_activity", nullable = false)
    private String mainActivity;

    @NotNull
    @Column(name = "acronym", nullable = false)
    private String acronym;

    @NotNull
    @Column(name = "company_created_date", nullable = false)
    private LocalDate companyCreatedDate;

    @NotNull
    @Column(name = "representative_uin", nullable = false)
    private String representativeUIN;

    @NotNull
    @Column(name = "representative_fullname", nullable = false)
    private String representativeFullname;

    @Column(name = "rid")
    private String rid;



    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrivateLegalEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((PrivateLegalEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrivateLegalEntity{" +
            "id=" + getId() +
            ", uin='" + getUin() + "'" +
            ", companyName='" + getCompanyName() + "'" +
            ", address='" + getAddress() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", secondaryPhoneNumber='" + getSecondaryPhoneNumber() + "'" +
            ", email='" + getEmail() + "'" +
            ", entityType='" + getEntityType() + "'" +
            ", identificationDocType='" + getIdentificationDocType() + "'" +
            ", identificationDocNumber='" + getIdentificationDocNumber() + "'" +
            ", identificationDocPhoto='" + getIdentificationDocPhoto() + "'" +
            ", identificationDocPhotoContentType='" + getIdentificationDocPhotoContentType() + "'" +
            ", mainActivity='" + getMainActivity() + "'" +
            ", acronym='" + getAcronym() + "'" +
            ", companyCreatedDate='" + getCompanyCreatedDate() + "'" +
            ", representativeUIN='" + getRepresentativeUIN() + "'" +
            ", representativeFullname='" + getRepresentativeFullname() + "'" +
            ", rid='" + getRid() + "'" +
            "}";
    }
}
