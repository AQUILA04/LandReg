package com.optimize.land.model.entity;

import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RegistrationStatus;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class Actor extends AbstractActor {
    public void addUin(String uin) {
        this.uin = uin;
        if (ActorType.PHYSICAL_PERSON.equals(this.type) && Objects.nonNull(this.physicalPerson)) {
            this.physicalPerson.uin = this.uin;
            this.physicalPerson.registrationStatus = RegistrationStatus.ACTOR;
        } else if (ActorType.INFORMAL_GROUP.equals(this.type) && Objects.nonNull(this.informalGroup)) {
            this.informalGroup.setUin(this.uin);
        } else if (ActorType.PRIVATE_LEGAL_ENTITY.equals(this.type) && Objects.nonNull(this.privateLegalEntity)) {
            this.privateLegalEntity.setUin(this.uin);
        } else {
            if(Objects.nonNull(this.publicLegalEntity)) {
                this.publicLegalEntity.setUin(this.uin);
            }
        }
        this.registrationStatus = RegistrationStatus.ACTOR;
        this.id = null;
        this.fingerprintStores = null;
    }

//    @PrePersist
//    public void setUp() {
//        this.id = null;
//        this.createdBy = null;
//        this.createdDate = null;
//    }
}
