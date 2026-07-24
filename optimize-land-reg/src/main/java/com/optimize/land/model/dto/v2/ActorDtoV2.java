package com.optimize.land.model.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.land.model.dto.InformalGroupDto;
import com.optimize.land.model.dto.PersonDto;
import com.optimize.land.model.dto.PrivateLegalEntityDto;
import com.optimize.land.model.dto.PublicLegalEntityDto;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RoleActor;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDtoV2 {

    protected Long id;
    @Valid
    protected PersonDto physicalPerson;
    @Valid
    protected InformalGroupDto informalGroup;
    @Valid
    protected PrivateLegalEntityDto privateLegalEntity;
    @Valid
    protected PublicLegalEntityDto publicLegalEntity;
    private String uin;
    protected String synchroBatchNumber;
    protected String synchroPacketNumber;
    protected RoleActor role;
    protected ActorType type;
    @Valid
    protected Set<FingerprintStoreV2Dto> fingerprintStores;

    public void validateUniqueActorType() {
        int nonNullCount = 0;
        if (physicalPerson != null) {
            nonNullCount++;
        }
        if (informalGroup != null) {
            nonNullCount++;
        }
        if (privateLegalEntity != null) {
            nonNullCount++;
        }
        if (publicLegalEntity != null) {
            nonNullCount++;
        }
        if (nonNullCount > 1) {
            throw new CustomValidationException(
                "Only one attribute can be non-null. Found multiple non-null attributes in ['physicalPerson', 'informalGroup', 'privateLegalEntity', 'publicLegalEntity']."
            );
        }
        if (physicalPerson != null) {
            type = ActorType.PHYSICAL_PERSON;
            physicalPerson.setSynchroBatchNumber(this.synchroBatchNumber);
            physicalPerson.setSynchroPacketNumber(this.synchroPacketNumber);
            physicalPerson.setRole(this.role.name());
        } else if (informalGroup != null) {
            type = ActorType.INFORMAL_GROUP;
        } else if (privateLegalEntity != null) {
            type = ActorType.PRIVATE_LEGAL_ENTITY;
        } else if (publicLegalEntity != null) {
            type = ActorType.PUBLIC_LEGAL_ENTITY;
        }
    }

    public PersonDto getPhysicalPerson() {
        if (Objects.nonNull(physicalPerson) && physicalPerson.isNull()) {
            return null;
        }
        return physicalPerson;
    }

    public InformalGroupDto getInformalGroup() {
        if (Objects.nonNull(informalGroup) && informalGroup.isNull()) {
            return null;
        }
        return informalGroup;
    }

    public PrivateLegalEntityDto getPrivateLegalEntity() {
        if (Objects.nonNull(privateLegalEntity) && privateLegalEntity.isNull()) {
            return null;
        }
        return privateLegalEntity;
    }

    public PublicLegalEntityDto getPublicLegalEntity() {
        if (Objects.nonNull(publicLegalEntity) && publicLegalEntity.isNull()) {
            return null;
        }
        return publicLegalEntity;
    }

    public Set<FingerprintStoreV2Dto> getFingerprintStores() {
        return fingerprintStores != null ? fingerprintStores : new HashSet<>();
    }
}
