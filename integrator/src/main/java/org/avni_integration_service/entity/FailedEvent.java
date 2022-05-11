package org.avni_integration_service.entity;

import javax.persistence.*;

@Entity
@Table(name = "failed_events")
public class FailedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
}
