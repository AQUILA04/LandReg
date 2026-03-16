package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.Base64Image;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class FingerprintStoreDto {
    private Long id;
    private String rid;
    private HandType handType;
    private Finger fingerName;
    @NotBlank
    @Base64Image
    private String fingerprintImage;
    private String fingerprintImageContentType;
    @NotBlank
    private String fingerStr;

    @JsonIgnore
    public Finger fingerNameFromString() {
        if (Objects.nonNull(fingerStr)) {
            String[] split = fingerStr.split(" ");
            switch (split[0]) {
                case "Index" :
                    return Finger.INDEX;
                case "Pouce" :
                    return Finger.THUMB;
                case "Majeur" :
                    return Finger.MIDDLE;
                case "Annulaire" :
                    return Finger.RING;
                case "Auriculaire" :
                    return Finger.LITTLE;
            }
        }
        return this.fingerName;
    }

    public HandType getHandTypeFromString() {
        if (Objects.nonNull(fingerStr)) {
            String[] split = fingerStr.split(" ");
            switch (split[1]) {
                case "Gauche":
                    return this.handType = HandType.LEFT;
                case "Droit":
                    return this.handType = HandType.RIGHT;
            }
        }
        return this.handType;
    }

    public boolean isNull() {
        return Objects.isNull(fingerprintImage);
    }

    @Override
    public String toString() {
        String toStringFingerprint = Objects.nonNull(fingerprintImage) && fingerprintImage.length() > 255 ? this.fingerprintImage.substring(0, 255) + "..." : "";
        return "FingerprintStoreDto{" +
                "id=" + id +
                ", rid='" + rid + '\'' +
                ", handType=" + handType +
                ", fingerName=" + fingerName +
                ", fingerprintImage='" + toStringFingerprint + '\'' +
                ", fingerprintImageContentType='" + fingerprintImageContentType + '\'' +
                ", fingerStr='" + fingerStr + '\'' +
                '}';
    }
}
