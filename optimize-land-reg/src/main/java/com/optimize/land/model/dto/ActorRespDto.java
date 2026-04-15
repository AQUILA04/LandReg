package com.optimize.land.model.dto;

import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.enumeration.RoleActor;

public record ActorRespDto(Long id,
                           String uin,
                           String name,
                           ActorType type,
                           RoleActor role,
                           RegistrationStatus status,
                           String statusObservation) {
}
