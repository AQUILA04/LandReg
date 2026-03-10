package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
import jakarta.persistence.*;
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

    @Size(min = 10, max = 15)
    @Column(name = "uin", length = 15, unique = true)
    private String uin;

    @NotNull
    @Column(name = "group_name", nullable = false)
    private String groupName;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Size(min = 8, max = 11)
    @Column(name = "phone_number", length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Size(min = 8, max = 11)
    @Column(name = "secondary_phone_number", length = 11)
    private String secondaryPhoneNumber;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "group_type", nullable = false)
    private String groupType;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "representative_uin", length = 15, nullable = false)
    private String representativeUIN;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(name = "representative_fullname", length = 80, nullable = false)
    private String representativeFullname;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "secondary_representative_uin", length = 15, nullable = false)
    private String secondaryRepresentativeUIN;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(name = "secondary_representative_fullname", length = 80, nullable = false)
    private String secondaryRepresentativeFullname;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "third_representative_uin", length = 15, nullable = false)
    private String thirdRepresentativeUIN;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(name = "third_representative_fullname", length = 80, nullable = false)
    private String thirdRepresentativeFullname;

    @Lob
    @Column(name = "mandate_photo", nullable = false)
    private byte[] mandatePhoto;

    @NotNull
    @Column(name = "mandate_photo_content_type", nullable = false)
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
