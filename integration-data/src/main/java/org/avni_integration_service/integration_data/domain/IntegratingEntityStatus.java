package org.avni_integration_service.integration_data.domain;

import org.avni_integration_service.integration_data.domain.framework.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "integrating_entity_status")
public class IntegratingEntityStatus extends BaseEntity {
    @Column(name = "read_upto_numeric")
    private Integer readUptoNumeric;

    @Column(name = "read_upto_date_time")
    private Date readUptoDateTime;

    @Column(name = "entity_type")
    private String entityType;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getReadUptoNumeric() {
        return readUptoNumeric;
    }

    public void setReadUptoNumeric(Integer readUptoNumeric) {
        this.readUptoNumeric = readUptoNumeric;
    }

    public Date getReadUptoDateTime() {
        return readUptoDateTime;
    }

    public void setReadUptoDateTime(Date readUptoDateTime) {
        this.readUptoDateTime = readUptoDateTime;
    }
}
