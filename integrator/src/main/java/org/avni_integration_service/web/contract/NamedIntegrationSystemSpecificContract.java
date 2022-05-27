package org.avni_integration_service.web.contract;

import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

public class NamedIntegrationSystemSpecificContract extends NamedEntityContract {
    private int integrationSystemId;

    public NamedIntegrationSystemSpecificContract(NamedIntegrationSpecificEntity entity) {
        super(entity);
        this.integrationSystemId = entity.getIntegrationSystem().getId();
    }

    public int getIntegrationSystemId() {
        return integrationSystemId;
    }

    public void setIntegrationSystemId(int integrationSystemId) {
        this.integrationSystemId = integrationSystemId;
    }
}
