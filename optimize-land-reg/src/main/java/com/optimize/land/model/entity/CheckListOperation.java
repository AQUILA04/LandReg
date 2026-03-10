package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.annotation.ExistsInDB;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A LandLawRegistration.
 */
@Entity
@Table(name = "check_list_operation")
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckListOperation extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "mayor_uin", nullable = false)
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du Maire n'existe pas !")
    private String mayorUIN;

    @NotNull
    //@Size(min = 10, max = 15)
    @Column(name = "traditional_chief_uin", nullable = false)
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du chef traditionnel n'existe pas !")
    private String traditionalChiefUIN;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "notable_uin", nullable = false)
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du notable n'existe pas !")
    private String notableUIN;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "geometer_uin", length = 15, nullable = false)
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du géomètre n'existe pas !")
    private String geometerUIN;

    @NotNull
    //@Size(min = 10, max = 15)
    @Column(name = "owner_uin", nullable = false)
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du propriétaire n'existe pas !")
    private String ownerUIN;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "checkListOperation", orphanRemoval = true)
    private Set<Bordering> borderingList = new HashSet<>();

    @Column(name = "interested_third_party_uin")
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU de la partie tiers intéressé n'existe pas !")
    private String interestedThirdPartyUIN;
    private String topographerUIN;
    private String socialLandAgentUIN;
    private String tiersUIN;
    private String tiersRole;

    @PrePersist
    public void setUp() {
        if (Objects.nonNull(borderingList)) {
            this.borderingList.forEach(bordering -> bordering.setCheckListOperation(this));
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckListOperation)) {
            return false;
        }
        return getId() != null && getId().equals(((CheckListOperation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LandLawRegistration{" +
            "id=" + getId() +
            ", mayorUIN='" + getMayorUIN() + "'" +
            ", traditionalChiefUIN='" + getTraditionalChiefUIN() + "'" +
            ", notableUIN='" + getNotableUIN() + "'" +
            ", geometerUIN='" + getGeometerUIN() + "'" +
            ", ownerUIN='" + getOwnerUIN() + "'" +
            ", interestedThirdPartyUIN='" + getInterestedThirdPartyUIN() + "'" +
            "}";
    }

    public void addBordering(Bordering bordering) {
        this.borderingList.add(bordering);
        bordering.setCheckListOperation(this);
    }

    public void removeChild(Bordering bordering) {
        borderingList.remove(bordering);
        bordering.setCheckListOperation(null); // Maintenir la synchronisation
    }

    public void setBorderingList(Set<Bordering> borderingList) {
        borderingList.forEach(this::addBordering);
    }
}
