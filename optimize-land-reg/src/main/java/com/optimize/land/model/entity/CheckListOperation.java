package com.optimize.land.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * A LandLawRegistration.
 */
@Entity
@Table(name = "check_list_operation")
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckListOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "mayor_uin", length = 15, nullable = false)
    private String mayorUIN;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "traditional_chief_uin", length = 15, nullable = false)
    private String traditionalChiefUIN;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "notable_uin", length = 15, nullable = false)
    private String notableUIN;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "geometer_uin", length = 15, nullable = false)
    private String geometerUIN;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "owner_uin", length = 15, nullable = false)
    private String ownerUIN;

    @OneToMany
    private Set<Bordering> borderingList;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "interested_third_party_uin", length = 15, nullable = false)
    private String interestedThirdPartyUIN;


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
}
