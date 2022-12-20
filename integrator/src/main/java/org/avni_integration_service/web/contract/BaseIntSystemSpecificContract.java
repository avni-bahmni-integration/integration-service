package org.avni_integration_service.web.contract;

public class BaseIntSystemSpecificContract extends BaseEntityContract {
    private int integrationSystemId;

    public int getIntegrationSystemId() {
        return integrationSystemId;
    }

    public void setIntegrationSystemId(int integrationSystemId) {
        this.integrationSystemId = integrationSystemId;
    }
}
