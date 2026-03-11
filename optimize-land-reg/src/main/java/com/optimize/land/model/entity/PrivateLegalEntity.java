package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.ValidPhoneNumber;
import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.annotation.ExistsInDB;
import com.optimize.land.model.enumeration.PrivateEntityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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


    @Column(name = "uin")
    private String uin;

    @NotNull
    @Size(min = 3, max = 60)
    @Column(name = "company_name", length = 60, nullable = false, unique = true)
    private String companyName;

    //@NotNull
    //@Size(min = 4, max = 200)
    @Column(name = "address", length = 200)
    private String address;

    //@NotNull
    //@Size(min = 8, max = 11)
    @Column(name = "phone_number", length = 11)
    //@ValidPhoneNumber
    private String phoneNumber;

    //@Size(min = 8, max = 11)
    @Column(name = "secondary_phone_number", length = 11)
    //@ValidPhoneNumber
    private String secondaryPhoneNumber;

    @Column(name = "email")
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    @NotNull(message = "Le type de la personne morale de droit privé est obligatoire !")
    private PrivateEntityType entityType;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    private IdentificationDoc identificationDoc;

    //@NotNull
    @Column(name = "main_activity", nullable = false)
    private String mainActivity;

    //@NotNull
    @Column(name = "acronym")
    private String acronym;

    //@NotNull
    @Column(name = "company_created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate companyCreatedDate;

    //@NotNull
    @Column(name = "representative_uin")
    //TODO: A remettre pour la phase prod, enlever actuellement pour la phase de test
    //@ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du représentant n'existe pas !")
    private String representativeUIN;

    //@NotNull
    @Column(name = "representative_fullname")
    private String representativeFullname;

    @Column(name = "rid")
    private String rid;

    @JsonProperty(value = "identificationDoc")
    public IdentificationDoc getIdentificationDoc() {
        if (Objects.nonNull(this.identificationDoc) && this.identificationDoc.isNull() ) {
            return null;
        }
        return identificationDoc;
    }



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
            ", mainActivity='" + getMainActivity() + "'" +
            ", acronym='" + getAcronym() + "'" +
            ", companyCreatedDate='" + getCompanyCreatedDate() + "'" +
            ", representativeUIN='" + getRepresentativeUIN() + "'" +
            ", representativeFullname='" + getRepresentativeFullname() + "'" +
            ", rid='" + getRid() + "'" +
            "}";
    }
}
