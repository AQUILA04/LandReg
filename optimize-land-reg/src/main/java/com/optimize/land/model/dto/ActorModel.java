package com.optimize.land.model.dto;

import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RoleActor;
import lombok.Data;

@Data
public class ActorModel {
    private String uin;
    private String name;
    private String firstname;
    private String lastname;
    private ActorType type;
    private RoleActor role;
    private String contact;
    protected String identificationDocType;
    protected String otherIdentificationDocType;
    protected String identificationDocNumber;
    private String address;
    private String email;

}
