package com.optimize.land.model.entity;

import com.optimize.common.entities.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OutboxEvent extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rid", nullable = false)
    private String rid;

    @Column(name = "event_type", nullable = false)
    private String eventType; // e.g., "LEGAL_ENTITY_FINGERPRINT_SYNC"

    @Column(name = "status", nullable = false)
    private String status; // e.g., "PENDING", "PROCESSED"

    public OutboxEvent(String rid, String eventType, String status) {
        this.rid = rid;
        this.eventType = eventType;
        this.status = status;
    }
}
