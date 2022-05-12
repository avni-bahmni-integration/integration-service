package org.avni_integration_service.bahmni.worker.bahmni;

import org.apache.log4j.Logger;
import org.avni_integration_service.integration_data.BahmniEntityType;
import org.avni_integration_service.integration_data.ConnectionFactory;
import org.avni_integration_service.integration_data.domain.BahmniEntityStatus;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.BahmniEntityStatusRepository;
import org.avni_integration_service.bahmni.repository.BaseOpenMRSRepository;
import org.avni_integration_service.bahmni.repository.openmrs.ImplementationConfigurationRepository;
import org.avni_integration_service.bahmni.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PatientFirstRunWorker implements PatientsProcessor {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private PatientEventWorker eventWorker;
    @Autowired
    private BahmniEntityStatusRepository bahmniEntityStatusRepository;
    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    private static final Logger logger = Logger.getLogger(PatientFirstRunWorker.class);

    public void processPatients() {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(implementationConfigurationRepository.getFirstRunPatientSql());
            BahmniEntityStatus bahmniEntityStatus = bahmniEntityStatusRepository.findByEntityType(BahmniEntityType.Patient);
            preparedStatement.setInt(1, bahmniEntityStatus.getReadUpto());
            preparedStatement.setFetchSize(20);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int patientId = resultSet.getInt(1);
                String patientUuid = resultSet.getString(2);
                eventWorker.process(new Event("0", String.format("/%s/patient/%s?v=full", BaseOpenMRSRepository.OPENMRS_BASE_PATH, patientUuid)));
                bahmniEntityStatus.setReadUpto(patientId);
                bahmniEntityStatusRepository.save(bahmniEntityStatus);
                logger.info(String.format("Completed patient id=%d, uuid:%s", patientId, patientUuid));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheRunImmutables(Constants constants) {
        eventWorker.cacheRunImmutables(constants);
    }
}
