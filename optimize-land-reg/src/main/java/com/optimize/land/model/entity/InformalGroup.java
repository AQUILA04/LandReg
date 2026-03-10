package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.ValidPhoneNumber;
import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.annotation.ExistsInDB;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * A InformalGroup.
 */
@Entity
@Table(name = "informal_group")
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InformalGroup extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uin", unique = true)
    private String uin;

    @NotNull(message = "Le nom du groupe informel est obligatoire !")
    @Column(name = "group_name", nullable = false, unique = true)
    private String groupName;

    //@NotNull
    @Column(name = "address", nullable = false)
    private String address;

    //@NotBlank(message = "Le numéro de téléphone du groupe informel est obligatoire !")
    //@Size(min = 8, max = 11)
    @Column(name = "phone_number", length = 11, unique = true)
    //@ValidPhoneNumber
    private String phoneNumber;

    //@Size(min = 8, max = 11)
    @Column(name = "secondary_phone_number", length = 11)
    //@ValidPhoneNumber
    private String secondaryPhoneNumber;

    //@NotNull
    @Column(name = "email")
    @Email
    private String email;

    @NotNull(message = "Le type de groupe informel est obligatoire !")
    @Column(name = "group_type", nullable = false)
    private String groupType;

    //@NotNull
    @Column(name = "representative_uin")
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du premier représentant n'existe pas !")
    private String representativeUIN;

    //@NotNull
    //@Size(min = 3, max = 80)
    @Column(name = "representative_fullname", length = 80)
    private String representativeFullname;

    //@NotNull
    @Column(name = "secondary_representative_uin")
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du second représentant n'existe pas !")
    private String secondaryRepresentativeUIN;

    //@NotNull
    //@Size(min = 3, max = 80)
    @Column(name = "secondary_representative_fullname", length = 80)
    private String secondaryRepresentativeFullname;

    //@NotNull
    @Column(name = "third_representative_uin")
    @ExistsInDB(entity = Actor.class, field = "uin", message = "le NIU du troisième représentant n'existe pas !")
    private String thirdRepresentativeUIN;

    //@NotNull
    //@Size(min = 3, max = 80)
    @Column(name = "third_representative_fullname", length = 80)
    private String thirdRepresentativeFullname;

    @Column(name = "mandate_photo")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Basic(fetch = FetchType.LAZY)
    private byte[] mandatePhoto;

    @Column(name = "mandate_photo_content_type")
    private String mandatePhotoContentType;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InformalGroup)) {
            return false;
        }
        return getId() != null && getId().equals(((InformalGroup) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InformalGroup{" +
            "id=" + getId() +
            ", uin='" + getUin() + "'" +
            ", groupName='" + getGroupName() + "'" +
            ", address='" + getAddress() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", secondaryPhoneNumber='" + getSecondaryPhoneNumber() + "'" +
            ", email='" + getEmail() + "'" +
            ", groupType='" + getGroupType() + "'" +
            ", representativeUIN='" + getRepresentativeUIN() + "'" +
            ", representativeFullname='" + getRepresentativeFullname() + "'" +
            ", secondaryRepresentativeUIN='" + getSecondaryRepresentativeUIN() + "'" +
            ", secondaryRepresentativeFullname='" + getSecondaryRepresentativeFullname() + "'" +
            ", thirdRepresentativeUIN='" + getThirdRepresentativeUIN() + "'" +
            ", thirdRepresentativeFullname='" + getThirdRepresentativeFullname() + "'" +
            ", mandatePhotoContentType='" + getMandatePhotoContentType() + "'" +
            "}";
    }
}
