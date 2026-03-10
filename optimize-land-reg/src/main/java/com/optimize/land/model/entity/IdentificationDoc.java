package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.NonEmptyByteArray;
import com.optimize.common.entities.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Entity
@Getter
@Setter
public class IdentificationDoc extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "identification_doc_type")
    protected String identificationDocType;

    @Column(name = "other_identification_doc_type")
    protected String otherIdentificationDocType;

    @Column(name = "identification_doc_number")
    protected String identificationDocNumber;

    @Column(name = "identification_doc_photo")
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Basic(fetch = FetchType.LAZY)
    @NonEmptyByteArray
    protected byte[] identificationDocPhoto;

    @Column(name = "identification_doc_photo_content_type")
    protected String identificationDocPhotoContentType;

    @OneToOne(mappedBy = "identificationDoc")
    @JsonBackReference
    protected Person person;

    @OneToOne(mappedBy = "identificationDoc")
    @JsonBackReference
    protected PrivateLegalEntity privateLegalEntity;

    @JsonIgnore
    public boolean isNull() {
        return !StringUtils.hasText(identificationDocNumber)  &&
                !StringUtils.hasText(identificationDocType) &&
                Objects.isNull(identificationDocPhoto);
    }
}
