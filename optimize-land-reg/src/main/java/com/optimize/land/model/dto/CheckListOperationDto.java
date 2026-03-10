package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.exception.CustomValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckListOperationDto {
    private Long id;
    @NotBlank(message = "Le NIU du maire est obligatoire !")
    private String mayorUIN;
    @NotBlank(message = "Le NIU du chef traditionnel est obligatoire")
    private String traditionalChiefUIN;
    @NotBlank(message = "Le NIU du notable est obligatoire !")
    private String notableUIN;
    @NotBlank(message = "Le NIU du géomètre est obligatoire !")
    private String geometerUIN;
    @NotBlank(message = "Le NIU du propriétaire ou du mandataire est obligatoire !")
    private String ownerUIN;
    @NotNull(message = "Les limitrophes sont obligatoires !")
    private Set<BorderingDto> borderingList;
    private String interestedThirdPartyUIN;
    @NotBlank(message = "Le NIU du Topographe est obligatoire !")
    private String topographerUIN;
    @NotBlank(message = "Le NIU de l'agent socio-foncier est obligatoire !")
    private String socialLandAgentUIN;
    private String tiersUIN;
    private String tiersRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckListOperationDto that = (CheckListOperationDto) o;
        return Objects.equals(mayorUIN, that.mayorUIN) &&
                Objects.equals(traditionalChiefUIN, that.traditionalChiefUIN) &&
                Objects.equals(notableUIN, that.notableUIN) &&
                Objects.equals(geometerUIN, that.geometerUIN) &&
                Objects.equals(ownerUIN, that.ownerUIN) &&
                Objects.equals(borderingList, that.borderingList) &&
                Objects.equals(interestedThirdPartyUIN, that.interestedThirdPartyUIN) &&
                Objects.equals(topographerUIN, that.topographerUIN) &&
                Objects.equals(socialLandAgentUIN, that.socialLandAgentUIN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mayorUIN, traditionalChiefUIN, notableUIN, geometerUIN, ownerUIN, borderingList, interestedThirdPartyUIN, topographerUIN, socialLandAgentUIN);
    }

    public void uniqueUINByActorCheck() {
        if (!isValid()) {
            throw new CustomValidationException("Un ou plusieurs NIU ont été utilisés pour deux roles différent pendant la checklist !!! NIU: "  );
        }
    }

    public boolean isValid() {
        Set<String> uniqueValues = new HashSet<>();
        if (interestedThirdPartyUIN != null) {
            uniqueValues.add(interestedThirdPartyUIN);
        }
        if (isUniqueCondition(uniqueValues, topographerUIN, socialLandAgentUIN, tiersUIN)) return false;
        if (isUniqueCondition(uniqueValues, ownerUIN, geometerUIN, notableUIN)) return false;

        if (traditionalChiefUIN != null && !uniqueValues.add(traditionalChiefUIN)) {
            return false;
        }

        return mayorUIN == null || uniqueValues.add(mayorUIN);
    }

    private boolean isUniqueCondition(Set<String> uniqueValues, String topographerUIN, String socialLandAgentUIN, String tiersUIN) {
        if (topographerUIN != null && !uniqueValues.add(topographerUIN)) {
            return true;
        }
        if (socialLandAgentUIN != null && !uniqueValues.add(socialLandAgentUIN)) {
            return true;
        }
        return tiersUIN != null && !uniqueValues.add(tiersUIN);
    }
}
