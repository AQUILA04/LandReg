package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.*;
import com.optimize.common.entities.entity.Auditable;
import com.optimize.land.annotation.ExistsInDB;
import com.optimize.land.model.dto.IdentificationDocDto;
import com.optimize.land.model.enumeration.MaritalStatus;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.enumeration.Sex;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(
        columnNames = {"lastname", "firstname", "sex", "marital_status", "birth_date",
                "place_of_birth", "nationality", "profession", "address", "primary_phone", "email"},
        name = "person_unique_constraint")})
@ConditionalNotNull(booleanField = "hasIDDoc", dependentField = "identificationDoc")
//@ConditionalNotNull(booleanField = "hasHandicap", dependentField = "handicapType")
@ConditionalNull
public class Person extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @NotNull
    @Size(min = 2, max = 25)
    @Column(name = "lastname", length = 25)
    protected String lastname;

    @NotNull
    @Size(min = 2, max = 55)
    @Column(name = "firstname", length = 55, nullable = false)
    protected String firstname;

    //@NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    //@DependentField
    protected Sex sex;

    @Column(name = "uin", unique = true)
    protected String uin;

    //@NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    //@DependentField
    protected MaritalStatus maritalStatus;

    //@NotNull
    @Column(name = "birth_date")
    @PastOrPresent
    //@DependentField
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate birthDate;

    //@NotNull
    //@Size(min = 2, max = 60)
    @Column(name = "place_of_birth", length = 60)
    //@DependentField
    protected String placeOfBirth;

    //@NotNull
    //@Size(min = 2, max = 60)
    @Column(name = "nationality", length = 60)
    @DependentField
    protected String nationality;

    @Column(name = "profession")
    //@DependentField
    protected String profession;

    @Column(name = "other_profession")
    protected String otherProfession;

    //@NotNull
    //@Size(min = 2, max = 70)
    @Column(name = "address", length = 70)
    //@DependentField
    protected String address;

    //@NotNull
    //@Size(min = 8, max = 11)
    @Column(name = "primary_phone", length = 11)
    //@ValidPhoneNumber
    protected String primaryPhone;

    //@Size(min = 8, max = 11)
    @Column(name = "secondary_phone", length = 11)
    //@ValidPhoneNumber
    protected String secondaryPhone;

    //@NotNull
    @Column(name = "email")
    @Email
    //@DependentField
    protected String email;

    @Column(name = "has_handicap")
    //@DependentField
    protected Boolean hasHandicap;

    @Column(name = "socio_cultural_group")
    //@DependentField
    protected String socioCulturalGroup;

    @Column(name = "handicap_type")
    protected String handicapType;

    @Column(name = "other_handicap_type")
    protected String otherHandicapType;
    @Column(name = "has_id_doc")
    //@DependentField
    protected Boolean hasIDDoc;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    protected IdentificationDoc identificationDoc;

    @Column(name = "witness_uin")
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du témoin n'existe pas !")
    protected String witnessUIN;

    @ConditionField(allowedValues = {"TOPOGRAPHER", "SOCIAL_LAND_AGENT", "TIERS"})
    protected String role;

    protected RegistrationStatus registrationStatus;

    @Column(name = "status_observation", columnDefinition = "TEXT")
    protected String statusObservation;

    @Column(name = "rid", unique = true)
    protected String rid;

    @Column(name = "synchro_batch_number", columnDefinition = "TEXT")
    protected String synchroBatchNumber;

    @Column(name = "synchro_packet_number", unique = true, columnDefinition = "TEXT")
    protected String synchroPacketNumber;

    public String getFullName() {
        return this.firstname + " " + this.lastname;
    }


    @PrePersist
    @PreUpdate
    public void setUp() {
        if (Objects.nonNull(hasIDDoc) && Boolean.FALSE.equals(hasIDDoc)) {
            this.identificationDoc = null;
        }

        if (List.of("TOPOGRAPHER", "SOCIAL_LAND_AGENT", "TIERS").contains(role)) {
            this.identificationDoc = null;
        }

        if (Objects.nonNull(this.identificationDoc) && this.identificationDoc.isNull()) {
            this.identificationDoc = null;
        }
    }

    @JsonProperty(value = "identificationDoc")
    public IdentificationDoc getIdentificationDoc() {
        if (Objects.nonNull(this.identificationDoc) && this.identificationDoc.isNull() ) {
            return null;
        }
        return identificationDoc;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", sex=" + sex +
                ", uin='" + uin + '\'' +
                ", maritalStatus=" + maritalStatus +
                ", birthDate=" + birthDate +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", nationality='" + nationality + '\'' +
                ", profession='" + profession + '\'' +
                ", otherProfession='" + otherProfession + '\'' +
                ", address='" + address + '\'' +
                ", primaryPhone='" + primaryPhone + '\'' +
                ", secondaryPhone='" + secondaryPhone + '\'' +
                ", email='" + email + '\'' +
                ", hasHandicap=" + hasHandicap +
                ", socioCulturalGroup='" + socioCulturalGroup + '\'' +
                ", handicapType='" + handicapType + '\'' +
                ", otherHandicapType='" + otherHandicapType + '\'' +
                ", hasIDDoc=" + hasIDDoc +
                ", witnessUIN='" + witnessUIN + '\'' +
                ", role='" + role + '\'' +
                ", registrationStatus=" + registrationStatus +
                ", statusObservation='" + statusObservation + '\'' +
                ", rid='" + rid + '\'' +
                ", synchroBatchNumber='" + synchroBatchNumber + '\'' +
                ", synchroPacketNumber='" + synchroPacketNumber + '\'' +
                '}';
    }
}
