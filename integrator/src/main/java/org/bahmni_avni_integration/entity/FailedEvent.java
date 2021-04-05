package org.bahmni_avni_integration.entity;

import javax.persistence.*;

@Entity
@Table(name = "failed_events")
public class FailedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
}