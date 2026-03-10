package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.Auditable;
import com.optimize.land.model.enumeration.RoleActor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Finding extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nup;
    private String region;
    private String prefecture;
    private String commune;
    private String canton;
    private String locality;
    private RoleActor personType;
    private String uin;
    private Boolean hasConflict;
    @OneToOne
    private CheckListOperation firstCheckListOperation;
    @OneToOne
    private CheckListOperation lastCheckListOperation;
    @OneToOne
    private Conflict conflict;

}
