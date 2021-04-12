package org.bahmni_avni_integration.worker.bahmni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.ConnectionFactory;
import org.bahmni_avni_integration.integration_data.domain.BahmniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.BahmniEntityStatusRepository;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEventWorker;
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
    private static final Logger logger = Logger.getLogger(PatientFirstRunWorker.class);

    public void processPatients(Constants allConstants) {
        eventWorker.setConstants(allConstants);
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    select p.patient_id, p2.uuid from patient_identifier
                    join patient p on patient_identifier.patient_id = p.patient_id
                    join person p2 on p.patient_id = p2.person_id
                    where p.voided = false and identifier like 'TRI%'
                      and p.patient_id > ?
                    order by p.patient_id asc""");
            BahmniEntityStatus bahmniEntityStatus = bahmniEntityStatusRepository.findByEntityType(BahmniEntityType.Patient);
            preparedStatement.setInt(1, bahmniEntityStatus.getReadUpto());
            preparedStatement.setFetchSize(20);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int patientId = resultSet.getInt(1);
                String patientUuid = resultSet.getString(2);
                eventWorker.process(new Event("0", String.format("/openmrs/ws/rest/v1/patient/%s?v=full", patientUuid)));
                bahmniEntityStatus.setReadUpto(patientId);
                bahmniEntityStatusRepository.save(bahmniEntityStatus);
                logger.info(String.format("Completed patient id=%d, uuid:%s", patientId, patientUuid));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}