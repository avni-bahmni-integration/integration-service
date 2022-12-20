package org.avni_integration_service.bahmni.worker.bahmni;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniDbConnectionFactory;
import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.bahmni.repository.BaseOpenMRSRepository;
import org.avni_integration_service.bahmni.repository.openmrs.ImplementationConfigurationRepository;
import org.avni_integration_service.bahmni.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
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
    private BahmniDbConnectionFactory connectionFactory;

    @Autowired
    private PatientEncounterEventWorker eventWorker;
    @Autowired
    private IntegratingEntityStatusRepository integratingEntityStatusRepository;

    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    private static final Logger logger = Logger.getLogger(LabResultWorker.class);

    public void processLabResults() {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(implementationConfigurationRepository.getLabEncounterSql());
            IntegratingEntityStatus integratingEntityStatus = integratingEntityStatusRepository.findByEntityType(BahmniEntityType.LabResult.name());
            preparedStatement.setInt(1, integratingEntityStatus.getReadUptoNumeric());
            preparedStatement.setFetchSize(20);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int encounterId = resultSet.getInt(1);
                String encounterUuid = resultSet.getString(2);
                Event event = new Event("0", String.format("/%s/encounter/%s?v=full", BaseOpenMRSRepository.OPENMRS_BASE_PATH, encounterUuid));
                event.setTitle("Encounter");
                eventWorker.process(event);
                integratingEntityStatus.setReadUptoNumeric(encounterId);
                integratingEntityStatusRepository.save(integratingEntityStatus);
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
