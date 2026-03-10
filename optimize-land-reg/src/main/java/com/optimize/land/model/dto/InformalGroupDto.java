package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.Base64Image;
import com.optimize.common.entities.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InformalGroupDto {
    private Long id;
    private String uin;
    @NotNull(message = "Le nom du groupe informel est obligatoire !")
    private String groupName;
    //@NotNull
    private String address;
    //@NotNull
    //@ValidPhoneNumber
    private String phoneNumber;
    //@ValidPhoneNumber
    private String secondaryPhoneNumber;
    //@NotNull
    private String email;
    @NotNull(message = "Le type du groupe informel est obligatoire !")
    private String groupType;
    //@NotNull
    private String representativeUIN;
    //@NotNull
    private String representativeFullname;
    //@NotNull
    private String secondaryRepresentativeUIN;
    //@NotNull
    private String secondaryRepresentativeFullname;
    //@NotNull
    private String thirdRepresentativeUIN;
    //@NotNull
    private String thirdRepresentativeFullname;
    @Base64Image
    private String mandatePhoto;
    private String mandatePhotoContentType;

    @JsonIgnore
    public boolean isNull() {
        return !StringUtils.hasText(groupName);
    }
}
