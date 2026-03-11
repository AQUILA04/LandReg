package com.optimize.land.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimize.common.entities.annotations.NonEmptyByteArray;
import com.optimize.common.entities.entity.Auditable;
import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FingerprintStore extends BaseEntity<String> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "fingerprintSequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "rid", nullable = false)
    private String rid;

    @Enumerated(EnumType.STRING)
    @Column(name = "hand_type")
    private HandType handType;

    @Enumerated(EnumType.STRING)
    @Column(name = "finger_name")
    private Finger fingerName;

    @Column(name = "fingerprint_image")
    @Basic(fetch = FetchType.LAZY)
    //@NonEmptyByteArray
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] fingerprintImage;

    @Column(name = "fingerprint_image_content_type")
    private String fingerprintImageContentType;
    private String fingerStr;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference
    private AbstractActor actor;
}
