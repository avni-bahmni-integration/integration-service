package org.bahmni_avni_integration.scheduler;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.SyncDirection;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.FailedEventRepository;
import org.bahmni_avni_integration.worker.ErrorRecordsWorker;
import org.bahmni_avni_integration.worker.avni.EnrolmentWorker;
import org.bahmni_avni_integration.worker.avni.ProgramEncounterWorker;
import org.bahmni_avni_integration.worker.avni.SubjectWorker;
import org.bahmni_avni_integration.worker.bahmni.*;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DisallowConcurrentExecution
public class MainJob implements Job {
    private static final Logger logger = Logger.getLogger(MainJob.class);

    @Autowired
    private PatientWorker patientWorker;
    @Autowired
    private PatientFirstRunWorker patientFirstRunWorker;

    @Autowired
    private PatientEncounterWorker patientEncounterWorker;
    @Autowired
    private PatientEncounterFirstRunWorker patientEncounterFirstRunWorker;

    @Autowired
    private SubjectWorker subjectWorker;

    @Autowired
    private EnrolmentWorker enrolmentWorker;

    @Autowired
    private ProgramEncounterWorker programEncounterWorker;

    @Autowired
    private ConstantsRepository constantsRepository;
    @Value("${app.tasks}")
    private String tasks;
    @Value("${app.first.run}")
    private boolean isFirstRun;

    @Autowired
    private FailedEventRepository failedEventRepository;

    @Autowired
    private ErrorRecordsWorker errorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    public void execute(JobExecutionContext context) {
        logger.info(String.format("Job ** {%s} ** fired @ {%s}", context.getJobDetail().getKey().getName(), context.getFireTime()));
        try {
            failedEventRepository.deleteAll();
            List<IntegrationTask> tasks = IntegrationTask.getTasks(this.tasks);
            Constants allConstants = constantsRepository.findAllConstants();

            if (hasTask(tasks, IntegrationTask.AvniSubject)) {
                subjectWorker.cacheRunImmutables(allConstants);
                subjectWorker.processSubjects();
            }
            if (hasTask(tasks, IntegrationTask.AvniEnrolment)) {
                enrolmentWorker.cacheRunImmutables(allConstants);
                enrolmentWorker.processEnrolments();
            }
            if (hasTask(tasks, IntegrationTask.AvniEnrolment)) {
                programEncounterWorker.cacheRunImmutables(allConstants);
                programEncounterWorker.processProgramEncounters();
            }
            if (hasTask(tasks, IntegrationTask.BahmniPatient))
                getPatientWorker(allConstants).processPatients();
            if (hasTask(tasks, IntegrationTask.BahmniEncounter))
                getPatientEncounterWorker(allConstants).processEncounters();
            if (hasTask(tasks, IntegrationTask.AvniErrorRecords))
                processErrorRecords(allConstants, SyncDirection.AvniToBahmni);
            if (hasTask(tasks, IntegrationTask.BahmniErrorRecords))
                processErrorRecords(allConstants, SyncDirection.BahmniToAvni);
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
        logger.info(String.format("Next job scheduled @ {%s}", context.getNextFireTime()));
    }

    private void processErrorRecords(Constants allConstants, SyncDirection avniToBahmni) {
        errorRecordsWorker.cacheRunImmutables(allConstants);
        errorRecordsWorker.process(avniToBahmni);
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
