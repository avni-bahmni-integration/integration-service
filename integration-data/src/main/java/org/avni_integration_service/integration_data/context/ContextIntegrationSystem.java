package org.avni_integration_service.integration_data.context;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;

// This class is used instead of Integration System because Integration System is domain entity which may be connected to the database and cause difficult diagnose issues when the use of this is all over the code and Integration System has other fields.
public class ContextIntegrationSystem {
    private int id;
    private IntegrationSystem.IntegrationSystemType systemType;
    private String name;

    public ContextIntegrationSystem(IntegrationSystem integrationSystem) {
        this.id = integrationSystem.getId();
        this.systemType = integrationSystem.getSystemType();
        this.name = integrationSystem.getName();
    }

    public int getId() {
        return id;
    }

    public IntegrationSystem.IntegrationSystemType getSystemType() {
        return systemType;
    }

    public String getName() {
        return name;
    }
}
