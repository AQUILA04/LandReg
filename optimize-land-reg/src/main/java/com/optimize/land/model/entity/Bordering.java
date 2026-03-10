package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
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
    private String uin;
    @ManyToOne
    private CheckListOperation checkListOperation;
}
