package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.Base64Image;
import com.optimize.common.entities.annotations.ValidPhoneNumber;
import com.optimize.land.model.entity.IdentificationDoc;
import com.optimize.land.model.enumeration.PrivateEntityType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateLegalEntityDto {
    private Long id;
    private String uin;
    @NotNull(message = "Le nom de la personne morale de droit privé est obligatoire !")
    private String companyName;
    //@NotNull
    private String address;
    //@NotNull
    //@ValidPhoneNumber
    private String phoneNumber;
    //@ValidPhoneNumber
    private String secondaryPhoneNumber;
    private String email;
    private PrivateEntityType entityType;
    private IdentificationDocDto identificationDoc;
    //@NotNull
    private String mainActivity;
    //@NotNull
    private String acronym;
    //@NotNull
    private LocalDate companyCreatedDate;
    //@NotNull
    private String representativeUIN;
    //@NotNull
    private String representativeFullname;
    private String rid;

    @JsonProperty(value = "identificationDoc")
    public IdentificationDocDto getIdentificationDoc() {
        if (Objects.nonNull(this.identificationDoc) && this.identificationDoc.isNull() ) {
            return null;
        }
        return identificationDoc;
    }

    public boolean isNull() {
        return !StringUtils.hasText(this.companyName);
    }
}
