package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.Base64Image;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentificationDocDto {
    private Long id;
    @NotBlank(message = "Le type de document d'identification est obligatoire !")
    protected String identificationDocType;
    protected String otherIdentificationDocType;
    @NotBlank(message = "Le numéro du document d'identification est obligatoire !")
    protected String identificationDocNumber;
    @NotBlank(message = "La photo du document d'identification est obligatoire !")
    @Base64Image
    protected String identificationDocPhoto;
    protected String identificationDocPhotoContentType;

    @JsonIgnore
    public boolean isNull() {
        return !StringUtils.hasText(identificationDocNumber)  &&
                !StringUtils.hasText(identificationDocType) &&
                Objects.isNull(identificationDocPhoto);
    }

    @Override
    public String toString() {
        String imageData = Objects.nonNull(identificationDocPhoto) && identificationDocPhoto.length() > 255 ? identificationDocPhoto.substring(0, 255) + "..." : "";
        return "IdentificationDocDto{" +
                "id=" + id +
                ", identificationDocType='" + identificationDocType + '\'' +
                ", otherIdentificationDocType='" + otherIdentificationDocType + '\'' +
                ", identificationDocNumber='" + identificationDocNumber + '\'' +
                ", identificationDocPhoto='" + imageData + '\'' +
                ", identificationDocPhotoContentType='" + identificationDocPhotoContentType + '\'' +
                '}';
    }
}
