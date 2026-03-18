package com.optimize.land.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimize.common.entities.annotations.ConditionalNotNull;
import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RoleActor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ConditionalNotNull(booleanField = "hasConflict", dependentField = "conflict")
public class FindingDto {
    private Long id;
    @NotBlank(message = "Le NUP est obligatoire !")
    private String nup;
    private String region;
    private String prefecture;
    private String commune;
    private String canton;
    private String locality;
    private ActorType personType;
    private String uin;
    @NotNull(message = "vous devez renseignez si la parcelle a un conflit ou pas !")
    private Boolean hasConflict;
    //@Valid
    //@NotNull(message = "Le checklist avant opération est obligatoire !")
    private CheckListOperationDto firstCheckListOperation;
    //@Valid
    //@NotNull(message = "Le checklist après opération est obligatoire !")
    private CheckListOperationDto lastCheckListOperation;
    private String surface;
    private ConflictDto conflict;
    private String synchroBatchNumber;
    private String synchroPacketNumber;
    private String landForm;

    public void validateFirstAndLastCheckListOperations() {
        if (!this.firstCheckListOperation.equals(this.lastCheckListOperation)) {
            throw new CustomValidationException("Le checkList avant opération n'est pas conforme au checkList après opération !");
        }
        this.firstCheckListOperation.uniqueUINByActorCheck();
        this.lastCheckListOperation.uniqueUINByActorCheck();
    }

    public void checkListShouldBeNonNullForCreation() {
        if (this.firstCheckListOperation == null || this.lastCheckListOperation == null) {
            throw new CustomValidationException("Les check-list sont obligatoire pour la création de constatation !");
        }
    }

}
