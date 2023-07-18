package org.avni_integration_service.web.contract;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;

public class IntegrationSystemContract {
    private final IntegrationSystem.IntegrationSystemType type;
    private final int id;
    private final String instanceName;

    public IntegrationSystemContract(IntegrationSystem integrationSystem) {
        this.type = integrationSystem.getSystemType();
        this.id = integrationSystem.getId();
        this.instanceName = integrationSystem.getName();
    }

    public IntegrationSystem.IntegrationSystemType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
