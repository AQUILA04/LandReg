package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.Auditable;
import com.optimize.land.model.enumeration.MaritalStatus;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.enumeration.RoleActor;
import com.optimize.land.model.enumeration.Sex;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractActor extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "actorSequenceGenerator")
    protected Long id;
    //@NotNull
    @Size(min = 2, max = 25)
    @Column(name = "lastname", length = 25, nullable = false)
    protected String lastname;

    //@NotNull
    @Size(min = 2, max = 55)
    @Column(name = "firstname", length = 55, nullable = false)
    protected String firstname;

    //@NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    protected Sex sex;

    //@NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = false)
    protected MaritalStatus maritalStatus;

    //@NotNull
    @Column(name = "birth_date", nullable = false)
    protected LocalDate birthDate;

    //@NotNull
    @Size(min = 2, max = 60)
    @Column(name = "place_of_birth", length = 60, nullable = false)
    protected String placeOfBirth;

    //@NotNull
    @Size(min = 2, max = 60)
    @Column(name = "nationality", length = 60, nullable = false)
    protected String nationality;

    @Column(name = "profession")
    protected String profession;

    @Column(name = "other_profession")
    protected String otherProfession;

    //@NotNull
    @Size(min = 2, max = 70)
    @Column(name = "address", length = 70, nullable = false)
    protected String address;

    //@NotNull
    @Size(min = 8, max = 11)
    @Column(name = "primary_phone", length = 11, nullable = false)
    protected String primaryPhone;

    @Size(min = 8, max = 11)
    @Column(name = "secondary_phone", length = 11)
    protected String secondaryPhone;

    //@NotNull
    @Column(name = "email", nullable = false)
    protected String email;

    @Column(name = "has_handicap")
    protected Boolean hasHandicap;

    @Column(name = "socio_cultural_group")
    protected String socioCulturalGroup;

    @Column(name = "handicap_type")
    protected String handicapType;

    @Column(name = "other_handicap_type")
    protected String otherHandicapType;

    @Column(name = "first_fingerprint")
    protected String firstFingerprint;

    @Column(name = "second_fingerprint")
    protected String secondFingerprint;

    @Column(name = "third_fingerprint")
    protected String thirdFingerprint;

    @Column(name = "first_finger_name")
    protected String firstFingerName;

    @Column(name = "second_finger_name")
    protected String secondFingerName;

    @Column(name = "third_finger_name")
    protected String thirdFingerName;

    @Column(name = "has_id_doc")
    protected Boolean hasIDDoc;

    @Column(name = "identification_doc_type")
    protected String identificationDocType;

    @Column(name = "other_identification_doc_type")
    protected String otherIdentificationDocType;

    @Column(name = "identification_doc_number")
    protected String identificationDocNumber;

    @Lob
    @Column(name = "identification_doc_photo")
    protected byte[] identificationDocPhoto;

    @Column(name = "identification_doc_photo_content_type")
    protected String identificationDocPhotoContentType;

    @Column(name = "witness_uin")
    protected String witnessUIN;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    protected RegistrationStatus registrationStatus;

    @Column(name = "status_observation")
    protected String statusObservation;

    @Column(name = "rid")
    protected String rid;

    @Column(name = "synchro_batch_number")
    protected String synchroBatchNumber;

    @Column(name = "synchro_packet_number")
    protected String synchroPacketNumber;

    @Enumerated(EnumType.STRING)
    protected RoleActor role;
}
