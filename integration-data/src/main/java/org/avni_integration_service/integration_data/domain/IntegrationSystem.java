package org.avni_integration_service.integration_data.domain;

import org.avni_integration_service.integration_data.domain.framework.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class IntegrationSystem extends BaseEntity {
    @Column(name = "system_type")
    @Enumerated(EnumType.STRING)
    private IntegrationSystemType systemType;

    @Column
    private String name;

    public IntegrationSystemType getSystemType() {
        return systemType;
    }

    public static enum IntegrationSystemType {
        Goonj, power, Amrit, bahmni
    }

    public String getName() {
        return name;
    }
}
