package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.Base64Image;
import com.optimize.land.model.enumeration.RoleActor;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BioAuthDto {
    @NotBlank(message = "Le NIU est obligatoire pour l'authentification biométrique !")
    private String uin;
    @Base64Image(message = "Le fingerprint est requis en format base64")
    private String fingerprint;
    private String rid;
    private RoleActor role;
}
