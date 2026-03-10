package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.Auditable;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FingerprintStore extends Auditable<String> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "fingerprintSequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "rid", nullable = false)
    private String rid;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "hand_type", nullable = false)
    private HandType handType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "finger_name", nullable = false)
    private Finger fingerName;

    @Lob
    @Column(name = "fingerprint_image")
    private byte[] fingerprintImage;

    @Column(name = "fingerprint_image_content_type")
    private String fingerprintImageContentType;
}
