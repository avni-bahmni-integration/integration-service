package org.avni_integration_service.integration_data.domain;

import org.avni_integration_service.integration_data.domain.framework.NamedEntity;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

import javax.persistence.Entity;

@Entity
public class MappingGroup extends NamedIntegrationSpecificEntity {
    public MappingGroup() {
    }

    public MappingGroup(String name) {
        this.setName(name);
    }
}
