package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.ValidPhoneNumber;
import com.optimize.land.model.enumeration.PublicEntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicLegalEntityDto {
    private Long id;
    private String uin;
    @NotNull(message = "Le type de la personne morale de droit publique est obligatoire !")
    private PublicEntityType publicEntityType;
    //@NotBlank
    //@ValidPhoneNumber
    private String phoneNumber;
    private String name;

    @JsonIgnore
    public boolean isNull() {
        return Objects.isNull(this.publicEntityType);
    }

}
