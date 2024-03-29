package org.bahmni_avni_integration.worker.bahmni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.ConnectionFactory;
import org.bahmni_avni_integration.integration_data.domain.BahmniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.BahmniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.BaseOpenMRSRepository;
import org.bahmni_avni_integration.repository.openmrs.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LabResultWorker {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private PatientEncounterEventWorker eventWorker;
    @Autowired
    private BahmniEntityStatusRepository bahmniEntityStatusRepository;

    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    private static final Logger logger = Logger.getLogger(LabResultWorker.class);

    public void processLabResults() {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(implementationConfigurationRepository.getLabEncounterSql());
            BahmniEntityStatus bahmniEntityStatus = bahmniEntityStatusRepository.findByEntityType(BahmniEntityType.LabResult);
            preparedStatement.setInt(1, bahmniEntityStatus.getReadUpto());
            preparedStatement.setFetchSize(20);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int encounterId = resultSet.getInt(1);
                String encounterUuid = resultSet.getString(2);
                Event event = new Event("0", String.format("/%s/encounter/%s?v=full", BaseOpenMRSRepository.OPENMRS_BASE_PATH, encounterUuid));
                event.setTitle("Encounter");
                eventWorker.process(event);
                bahmniEntityStatus.setReadUpto(encounterId);
                bahmniEntityStatusRepository.save(bahmniEntityStatus);
                logger.info(String.format("Completed encounter id=%d, uuid:%s", encounterId, encounterUuid));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheRunImmutables(Constants constants) {
        eventWorker.cacheRunImmutables(constants);
    }
}
