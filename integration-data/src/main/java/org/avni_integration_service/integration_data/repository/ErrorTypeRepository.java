package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorTypeRepository extends BaseRepository<ErrorType> {

    ErrorType findByNameAndIntegrationSystem(String name, IntegrationSystem integrationSystem);
    ErrorType findByNameAndIntegrationSystemId(String name, int integrationSystemId);
    List<ErrorType> findAllByIntegrationSystemId(int id);
    List<ErrorType> findAllByIntegrationSystem(IntegrationSystem integrationSystem);
}
