package org.avni_integration_service.integration_data.repository.config;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.config.IntegrationSystemConfig;
import org.avni_integration_service.integration_data.domain.config.IntegrationSystemConfigCollection;
import org.avni_integration_service.integration_data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationSystemConfigRepository extends BaseRepository<IntegrationSystemConfig> {
    List<IntegrationSystemConfig> getAllByIntegrationSystem(IntegrationSystem integrationSystem);

    default IntegrationSystemConfigCollection getInstanceConfiguration(IntegrationSystem integrationSystem) {
        return new IntegrationSystemConfigCollection(this.getAllByIntegrationSystem(integrationSystem));
    }
}
