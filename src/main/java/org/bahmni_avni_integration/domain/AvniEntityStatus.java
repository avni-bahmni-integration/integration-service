package org.bahmni_avni_integration.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "avni_entity_status")
public class AvniEntityStatus extends BaseEntity {
    @Column(name = "read_upto", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date readUpto;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private AvniEntityType entityType;

    public Date getReadUpto() {
        return readUpto;
    }

    public void setReadUpto(Date readUpto) {
        this.readUpto = readUpto;
    }

    public AvniEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(AvniEntityType entityType) {
        this.entityType = entityType;
    }
}