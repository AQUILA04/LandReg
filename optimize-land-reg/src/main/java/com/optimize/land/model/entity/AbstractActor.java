package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.entity.Auditable;
import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.land.model.dto.ActorModel;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.enumeration.RoleActor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractActor extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@SequenceGenerator(name = "actorSequenceGenerator")
    protected Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected Person physicalPerson;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected InformalGroup informalGroup;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected PrivateLegalEntity privateLegalEntity;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected PublicLegalEntity publicLegalEntity;

    @Column(name = "uin", unique = true)
    protected String uin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    protected RegistrationStatus registrationStatus;

    @Column(name = "status_observation", columnDefinition = "TEXT")
    protected String statusObservation;

    @Column(name = "rid")
    protected String rid;

    @Column(name = "synchro_batch_number", columnDefinition = "TEXT")
    protected String synchroBatchNumber;

    @Column(name = "synchro_packet_number", columnDefinition = "TEXT")
    protected String synchroPacketNumber;

    @Enumerated(EnumType.STRING)
    protected RoleActor role;

    @Enumerated(EnumType.STRING)
    protected ActorType type;
    private String operatorAgent;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "actor")
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonManagedReference
    protected Set<FingerprintStore> fingerprintStores = new HashSet<>();

    public void validateUniqueActorType () {
            if (Objects.nonNull(physicalPerson) &&
                    Objects.nonNull(informalGroup) &&
                    Objects.nonNull(privateLegalEntity) && Objects.nonNull(publicLegalEntity)) {
                throw new CustomValidationException("Au moins une valeur pour le type d'acteur est obligatoire !");
            }
    }

    public void updateFingerprint() {
        fingerprintStores.forEach(fs -> fs.setActor(this));
    }

    public void addRid(String rid) {
        this.rid = rid;
        this.registrationStatus = RegistrationStatus.PENDING;
        if (Objects.nonNull(fingerprintStores)) {
            fingerprintStores.forEach(fs -> fs.setRid(rid));
        }

    }

    @JsonIgnore
    public ActorModel toActorModel() {
        ActorModel model = new ActorModel();
        model.setUin(this.uin);
        model.setType(this.type);
        model.setRole(this.role);
        if (actorTypeIs(ActorType.PHYSICAL_PERSON)) {
            model.setName(this.physicalPerson.getFullName());
            model.setFirstname(this.physicalPerson.getFirstname());
            model.setLastname(this.physicalPerson.getLastname());
            model.setContact(this.physicalPerson.getPrimaryPhone());
            model.setAddress(this.physicalPerson.getAddress());
            model.setEmail(this.physicalPerson.getEmail());
            if (Objects.nonNull(this.physicalPerson.getIdentificationDoc())) {
                model.setIdentificationDocType(this.physicalPerson.getIdentificationDoc().getIdentificationDocType());
                model.setIdentificationDocNumber(this.physicalPerson.getIdentificationDoc().getIdentificationDocNumber());
                model.setOtherIdentificationDocType(this.physicalPerson.getIdentificationDoc().getOtherIdentificationDocType());
            }

        } else if (actorTypeIs(ActorType.INFORMAL_GROUP)) {
            model.setName(this.informalGroup.getGroupName());
            model.setContact(this.informalGroup.getPhoneNumber());
            model.setEmail(this.informalGroup.getEmail());
        } else if (actorTypeIs(ActorType.PRIVATE_LEGAL_ENTITY)) {
            model.setName(this.privateLegalEntity.getCompanyName());
            model.setContact(this.privateLegalEntity.getPhoneNumber());
            model.setAddress(this.privateLegalEntity.getAddress());
            model.setEmail(this.privateLegalEntity.getEmail());
            if (Objects.nonNull(this.privateLegalEntity.getIdentificationDoc())) {
                model.setIdentificationDocType(this.privateLegalEntity.getIdentificationDoc().getIdentificationDocType());
                model.setIdentificationDocNumber(this.privateLegalEntity.getIdentificationDoc().getIdentificationDocNumber());
                model.setOtherIdentificationDocType(this.privateLegalEntity.getIdentificationDoc().getOtherIdentificationDocType());
            }
        } else {
            model.setContact(this.publicLegalEntity.getPhoneNumber());
            model.setName(Objects.nonNull(this.publicLegalEntity.getName()) ? this.publicLegalEntity.getName() : this.privateLegalEntity.getEntityType().name());
        }
        return model;
    }

    public boolean actorTypeIs(ActorType actorType) {
        return actorType.equals(type);
    }

    @JsonIgnore
    public void getAllOperations() {
        this.informalGroup = null;
        this.physicalPerson = null;
        this.privateLegalEntity = null;
        this.publicLegalEntity = null;
    }

    public void fingerprintMandatoryCheck() {
        if (actorTypeIs(ActorType.PHYSICAL_PERSON) && Objects.isNull(this.fingerprintStores)) {
            throw new CustomValidationException("Les données d'empreintes digitales sont obligatoires pour une personne physique !");
        }
    }

    @Override
    public String toString() {
        return "AbstractActor{" +
                "id=" + id +
                ", physicalPerson=" + physicalPerson +
                ", informalGroup=" + informalGroup +
                ", privateLegalEntity=" + privateLegalEntity +
                ", publicLegalEntity=" + publicLegalEntity +
                ", uin='" + uin + '\'' +
                ", registrationStatus=" + registrationStatus +
                ", statusObservation='" + statusObservation + '\'' +
                ", rid='" + rid + '\'' +
                ", synchroBatchNumber='" + synchroBatchNumber + '\'' +
                ", synchroPacketNumber='" + synchroPacketNumber + '\'' +
                ", role=" + role +
                ", type=" + type +
                ", operatorAgent='" + operatorAgent + '\'' +
                '}';
    }
}
