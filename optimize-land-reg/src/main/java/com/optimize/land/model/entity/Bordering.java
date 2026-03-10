package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.annotation.ExistsInDB;
import com.optimize.land.model.enumeration.CardinalPoint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Bordering extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CardinalPoint cardinalPoint;
    @NotBlank
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU d'un des limitrophe n'existe pas !")
    private String uin;
    @JsonBackReference
    @ManyToOne
    private CheckListOperation checkListOperation;
}
