package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.land.annotation.ExistsInDB;
import com.optimize.land.model.entity.Actor;
import com.optimize.land.model.enumeration.CardinalPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class BorderingDto {
    private Long id;
    @NotNull(message = "Le point cardinal d'un limitrophe est obligatoire !")
    private CardinalPoint cardinalPoint;
    @NotBlank(message = "Le NIU d'un limitrophe est obligatoire !")
    @ExistsInDB(entity = Actor.class, field = "uin", message = "Le NIU renseigner pour un limitrophe n'existe pas dans le système !")
    private String uin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorderingDto that = (BorderingDto) o;
        return cardinalPoint == that.cardinalPoint && Objects.equals(uin, that.uin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardinalPoint, uin);
    }
}
