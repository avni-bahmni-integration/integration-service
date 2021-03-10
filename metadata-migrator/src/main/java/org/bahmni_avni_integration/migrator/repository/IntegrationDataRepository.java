package org.bahmni_avni_integration.migrator.repository;

import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.List;

@Component
public class IntegrationDataRepository {
    @Autowired
    private ConnectionFactory connectionFactory;

    public void createBahmniToAvniFormMapping(List<OpenMRSForm> forms) {
        Connection connection = connectionFactory.getIntegrationDbConnection();

    }
}