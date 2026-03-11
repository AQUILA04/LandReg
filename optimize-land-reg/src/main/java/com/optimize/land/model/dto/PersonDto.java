package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.Base64Image;
import com.optimize.common.entities.annotations.ValidPhoneNumber;
import com.optimize.land.model.enumeration.MaritalStatus;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.enumeration.Sex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDto {
    protected Long id;
    //@NotBlank(message = "actor.person.lastname.mandatory")
    @NotBlank(message = "Le nom de l'acteur est obligatoire !")
    protected String lastname;
    //@NotBlank(message = "actor.person.firstname.mandatory")
    @NotBlank(message = "Le prénom de l'acteur est obligatoire !")
    protected String firstname;
    //@NotNull(message = "actor.person.sex.mandatory")
    protected Sex sex;
    //@NotNull(message = "actor.person.marital-status.mandatory")
    protected MaritalStatus maritalStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    //@NotNull(message = "actor.person.birth-date.mandatory")
    @PastOrPresent
    protected LocalDate birthDate;
    //@NotBlank(message = "actor.person.place-of-birth.mandatory")
    protected String placeOfBirth;
    //@NotBlank(message = "actor.person.nationality.mandatory")
    protected String nationality;
    //@NotBlank(message = "actor.person.profession.mandatory")
    protected String profession;
    protected String otherProfession;
    //@NotBlank(message = "actor.person.address.mandatory")
    protected String address;
    //@NotBlank(message = "actor.person.primary-phone.mandatory")
    //@ValidPhoneNumber
    protected String primaryPhone;
    //@ValidPhoneNumber
    protected String secondaryPhone;
    //@NotBlank(message = "actor.person.email.mandatory")
    @Email
    protected String email;

    protected Boolean hasHandicap = Boolean.FALSE;
    //@NotBlank(message = "actor.person.socio-cultural-group.mandatory")
    protected String socioCulturalGroup;
    protected String handicapType;
    protected String otherHandicapType;
    protected Boolean hasIDDoc;
    protected IdentificationDocDto identificationDoc;
    protected String witnessUIN;
    protected String role;
    protected String statusObservation;
    protected String rid;
    protected String synchroBatchNumber;
    protected String synchroPacketNumber;

    @JsonProperty(value = "identificationDoc")
    public IdentificationDocDto getIdentificationDoc() {
        if (Objects.nonNull(this.identificationDoc) && this.identificationDoc.isNull() ) {
            return null;
        }
        return identificationDoc;
    }

    @Override
    public String toString() {
        return "PersonDto{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", sex=" + sex +
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
                ", statusObservation='" + statusObservation + '\'' +
                ", rid='" + rid + '\'' +
                ", synchroBatchNumber='" + synchroBatchNumber + '\'' +
                ", synchroPacketNumber='" + synchroPacketNumber + '\'' +
                '}';
    }

    @JsonIgnore
    public boolean isNull() {
        return !StringUtils.hasText(this.firstname) && !StringUtils.hasText(this.lastname);
    }
}
