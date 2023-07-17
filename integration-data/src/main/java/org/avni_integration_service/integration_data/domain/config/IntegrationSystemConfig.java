package org.avni_integration_service.integration_data.domain.config;

import org.avni_integration_service.integration_data.domain.framework.BaseIntegrationSpecificEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class IntegrationSystemConfig extends BaseIntegrationSpecificEntity {
    @Column
    private String key;
    @Column
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
