package org.avni_integration_service.integration_data.domain.framework;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseIntegrationSpecificEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_system_id")
    private IntegrationSystem integrationSystem;

    public IntegrationSystem getIntegrationSystem() {
        return integrationSystem;
    }

    public void setIntegrationSystem(IntegrationSystem integrationSystem) {
        this.integrationSystem = integrationSystem;
    }
}
