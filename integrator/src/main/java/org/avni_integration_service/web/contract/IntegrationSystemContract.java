package org.avni_integration_service.web.contract;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;

public class IntegrationSystemContract {
    private IntegrationSystem.IntegrationSystemType type;
    private int id;
    private String instanceName;

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
