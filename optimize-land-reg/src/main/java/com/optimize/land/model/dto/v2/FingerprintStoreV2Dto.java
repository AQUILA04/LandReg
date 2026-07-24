package com.optimize.land.model.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FingerprintStoreV2Dto {

    private Long id;
    private String rid;
    private HandType handType;
    private Finger fingerName;
    @NotBlank
    private String fingerStr;
    /** Optional explicit ordering when multiple fingerprint parts are sent. */
    private Integer partIndex;

    public Finger fingerNameFromString() {
        if (Objects.nonNull(fingerStr)) {
            String[] split = fingerStr.split(" ");
            return switch (split[0]) {
                case "Index" -> Finger.INDEX;
                case "Pouce" -> Finger.THUMB;
                case "Majeur" -> Finger.MIDDLE;
                case "Annulaire" -> Finger.RING;
                case "Auriculaire" -> Finger.LITTLE;
                default -> this.fingerName;
            };
        }
        return this.fingerName;
    }

    public HandType getHandTypeFromString() {
        if (Objects.nonNull(fingerStr)) {
            String[] split = fingerStr.split(" ");
            if (split.length > 1) {
                return switch (split[1]) {
                    case "Gauche" -> HandType.LEFT;
                    case "Droit" -> HandType.RIGHT;
                    default -> this.handType;
                };
            }
        }
        return this.handType;
    }
}
