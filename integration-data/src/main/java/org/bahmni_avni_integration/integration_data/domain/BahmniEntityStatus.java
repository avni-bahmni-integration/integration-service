package org.bahmni_avni_integration.integration_data.domain;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bahmni_entity_status")
public class BahmniEntityStatus extends BaseEntity {
    @Column(name = "read_upto", nullable = false)
    private int readUpto;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private BahmniEntityType entityType;

    public int getReadUpto() {
        return readUpto;
    }

    public void setReadUpto(int readUpto) {
        this.readUpto = readUpto;
    }

    public BahmniEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(BahmniEntityType entityType) {
        this.entityType = entityType;
    }
}