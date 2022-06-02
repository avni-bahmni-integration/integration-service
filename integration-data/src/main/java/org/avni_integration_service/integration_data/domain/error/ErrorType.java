package org.avni_integration_service.integration_data.domain.error;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;

@Entity
public class ErrorType extends NamedIntegrationSpecificEntity {
    public ErrorType() {
    }
    public ErrorType(String name, IntegrationSystem integrationSystem) {
        this.setName(name);
        this.setIntegrationSystem(integrationSystem);
    }
}
