package org.avni_integration_service.integration_data.domain;

import javax.persistence.*;

@Entity
@Table(name = "integrating_entity_status")
public class IntegratingEntityStatus extends BaseEntity {
    @Column(name = "read_upto", nullable = false)
    private int readUpto;

    @Column(name = "entity_type")
    private String entityType;

    public int getReadUpto() {
        return readUpto;
    }

    public void setReadUpto(int readUpto) {
        this.readUpto = readUpto;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
