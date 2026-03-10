package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.optimize.common.entities.annotations.ConditionalNotNull;
import com.optimize.common.entities.entity.Auditable;
import com.optimize.land.model.enumeration.ActorType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@ConditionalNotNull(
        booleanField = "hasConflict",
        dependentField = "conflict",
        message = "Le valeur du conflit est obligatoire lorsque 'hasConflict' est égale à 'true' !"
)
public class Finding extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Le numéro unique parcellaire est obligatoire !")
    private String nup;
    private String region;
    private String prefecture;
    private String commune;
    @NotBlank(message = "Le canton est obligatoire pour la constatation !")
    private String canton;
    @NotBlank(message = "La localité ou village de la constatation est obligatoire !")
    private String locality;
    @Enumerated(EnumType.STRING)
    private ActorType personType;
    @NotBlank(message = "Le numéro d'identification unique du propriétaire est obligatoire !")
    private String uin;
    @NotNull(message = "vous devez renseignez si la parcelle a un conflit ou pas !")
    private Boolean hasConflict;
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private CheckListOperation firstCheckListOperation;
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private CheckListOperation lastCheckListOperation;
    @JsonManagedReference
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private Conflict conflict;
    private String surface;
    private String landForm;
    private String synchroBatchNumber;
    private String synchroPacketNumber;
    private String operatorAgent;

}
