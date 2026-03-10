package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.model.enumeration.PublicEntityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * A PublicLegalEntity.
 */
@Entity
@Table(name = "public_legal_entity")
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicLegalEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(min = 10, max = 15)
    @Column(name = "uin", length = 15, unique = true)
    private String uin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "public_entity_type", nullable = false)
    private PublicEntityType publicEntityType;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicLegalEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((PublicLegalEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicLegalEntity{" +
            "id=" + getId() +
            ", uin='" + getUin() + "'" +
            ", publicEntityType='" + getPublicEntityType() + "'" +
            "}";
    }
}
