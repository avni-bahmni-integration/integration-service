package org.avni_integration_service.bahmni.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.bahmni.client.BahmniAvniSessionFactory;
import org.avni_integration_service.bahmni.worker.AvniBahmniErrorRecordsWorker;
import org.avni_integration_service.bahmni.worker.avni.EnrolmentWorker;
import org.avni_integration_service.bahmni.worker.avni.GeneralEncounterWorker;
import org.avni_integration_service.bahmni.worker.avni.ProgramEncounterWorker;
import org.avni_integration_service.bahmni.worker.avni.SubjectWorker;
import org.avni_integration_service.bahmni.worker.bahmni.*;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.util.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class AvniBahmniMainJob {
    private static final Logger logger = Logger.getLogger(AvniBahmniMainJob.class);
    
    @Value("${bahmni.healthcheck.slug}")
    private String healthCheckSlug;

    @Autowired
    private PatientWorker patientWorker;
    @Autowired
    private PatientFirstRunWorker patientFirstRunWorker;

    @Autowired
    private PatientEncounterWorker patientEncounterWorker;
    @Autowired
    private PatientEncounterFirstRunWorker patientEncounterFirstRunWorker;

    @Autowired
    private LabResultWorker labResultWorker;

    @Autowired
    private SubjectWorker subjectWorker;

    @Autowired
    private EnrolmentWorker enrolmentWorker;

    @Autowired
    private ProgramEncounterWorker programEncounterWorker;

    @Autowired
    private GeneralEncounterWorker generalEncounterWorker;

    @Autowired
    private ConstantsRepository constantsRepository;
    @Value("${bahmni.app.tasks}")
    private String tasks;
    @Value("${bahmni.app.first.run}")
    private boolean isFirstRun;

    @Autowired
    private AvniBahmniErrorRecordsWorker avniBahmniErrorRecordsWorker;

    @Autowired
    private BahmniEntityDateWorker bahmniEntityDateWorker;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private AvniHttpClient avniHttpClient;

    @Autowired
    private BahmniAvniSessionFactory bahmniAvniSessionFactory;

    public void execute() {
        try {
            avniHttpClient.setAvniSession(bahmniAvniSessionFactory.createSession());
            List<IntegrationTask> tasks = IntegrationTask.getTasks(this.tasks);
            Constants allConstants = constantsRepository.findAllConstants();

            if (hasTask(tasks, IntegrationTask.AvniSubject)) {
                logger.info("Processing AvniSubject");
                subjectWorker.cacheRunImmutables(allConstants);
                subjectWorker.processSubjects();
            }
            if (hasTask(tasks, IntegrationTask.AvniEnrolment)) {
                logger.info("Processing AvniEnrolment");
                enrolmentWorker.cacheRunImmutables(allConstants);
                enrolmentWorker.processEnrolments();
            }
            if (hasTask(tasks, IntegrationTask.AvniProgramEncounter)) {
                logger.info("Processing AvniProgramEncounter");
                programEncounterWorker.cacheRunImmutables(allConstants);
                programEncounterWorker.processProgramEncounters();
            }
            if (hasTask(tasks, IntegrationTask.AvniGeneralEncounter)) {
                logger.info("Processing AvniGeneralEncounter");
                generalEncounterWorker.cacheRunImmutables(allConstants);
                generalEncounterWorker.processEncounters();
            }
            if (hasTask(tasks, IntegrationTask.BahmniPatient)) {
                logger.info("Processing BahmniPatient");
                getPatientWorker(allConstants).processPatients();
            }
            if (hasTask(tasks, IntegrationTask.BahmniEncounter)) {
                logger.info("Processing BahmniEncounter");
                getPatientEncounterWorker(allConstants).processEncounters();
            }
            if (hasTask(tasks, IntegrationTask.BahmniLabResult)) {
                logger.info("Processing BahmniLabResult");
                labResultWorker.cacheRunImmutables(allConstants);
                labResultWorker.processLabResults();
            }
            if (hasTask(tasks, IntegrationTask.AvniErrorRecords)) {
                logger.info("Processing AvniErrorRecords");
                processErrorRecords(allConstants, SyncDirection.AvniToBahmni);
            }
            if (hasTask(tasks, IntegrationTask.BahmniErrorRecords)) {
                logger.info("Processing BahmniErrorRecords");
                processErrorRecords(allConstants, SyncDirection.BahmniToAvni);
            }
            if (hasTask(tasks, IntegrationTask.BahmniVisitDateFix)) {
                logger.info("Processing BahmniVisitDateFix");
                fixBahmniVisitAndEncounterDates();
            }
            healthCheckService.success(healthCheckSlug);
        } catch (Exception e) {
            healthCheckService.failure(healthCheckSlug);
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
    }

    private void fixBahmniVisitAndEncounterDates() throws SQLException {
        bahmniEntityDateWorker.fixVisitDates();
    }

    private void processErrorRecords(Constants allConstants, SyncDirection syncDirection) throws Exception {
        avniBahmniErrorRecordsWorker.cacheRunImmutables(allConstants);
        avniBahmniErrorRecordsWorker.process(syncDirection, false);
    }

    public PatientEncounterFirstRunWorker getPatientEncounterFirstRunWorker(Constants constants) {
        patientEncounterFirstRunWorker.cacheRunImmutables(constants);
        return patientEncounterFirstRunWorker;
    }

    private PatientEncountersProcessor getPatientEncounterWorker(Constants constants) {
        if (isFirstRun) {
            patientEncounterFirstRunWorker.cacheRunImmutables(constants);
            return patientEncounterFirstRunWorker;
        } else {
            patientEncounterWorker.cacheRunImmutables(constants);
            return patientEncounterWorker;
        }
    }

    private PatientsProcessor getPatientWorker(Constants constants) {
        if (isFirstRun) {
            patientFirstRunWorker.cacheRunImmutables(constants);
            return patientFirstRunWorker;
        } else {
            patientWorker.cacheRunImmutables(constants);
            return patientWorker;
        }
    }

    private boolean hasTask(List<IntegrationTask> tasks, IntegrationTask task) {
        return tasks.stream().filter(integrationTask -> integrationTask.equals(task)).findAny().orElse(null) != null;
    }
}
