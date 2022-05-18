package org.avni_integration_service.integration_data.domain;

import org.avni_integration_service.integration_data.domain.framework.BaseIntegrationSpecificEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class IntegrationEntityType extends BaseIntegrationSpecificEntity {
    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
