package org.avni_integration_service.amrit.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritAvniSessionFactory;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.worker.AmritEnrolmentWorker;
import org.avni_integration_service.amrit.worker.AmritErrorRecordWorker;
import org.avni_integration_service.amrit.worker.BeneficiaryWorker;
import org.avni_integration_service.amrit.worker.HouseholdWorker;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.util.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvniAmritMainJob {

    private static final Logger logger = Logger.getLogger(AvniAmritMainJob.class);

    @Value("${healthcheck.mainJob}")
    private String mainJobId;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private BeneficiaryWorker beneficiaryWorker;

    @Autowired
    private HouseholdWorker householdWorker;

    @Autowired
    private AmritEnrolmentWorker amritEnrolmentWorker;

    @Autowired
    AmritAvniSessionFactory amritAvniSessionFactory;

    @Autowired
    private AvniHttpClient avniHttpClient;

    @Value("${amrit.app.tasks}")
    private String tasks;

    @Autowired
    private AmritErrorRecordWorker errorRecordsWorker;

    public void execute() {
        try {
            logger.info("Starting the Amrit entities pull from Avni");
            avniHttpClient.setAvniSession(amritAvniSessionFactory.createSession());
            List<IntegrationTask> tasks = IntegrationTask.getTasks(this.tasks);
            processBeneficiaryAndBeneficiaryScan(tasks);
            processHousehold(tasks);
            processBornBirthAndCBAC(tasks);
            processErrors(tasks);
        } catch (Throwable e) {
            logger.error("Failed AvniAmritMainJob", e);
            bugsnag.notify(e);
        } finally {
            healthCheckService.verify(mainJobId);
        }
    }

    private void processBeneficiaryAndBeneficiaryScan(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.Beneficiary)) {
                logger.info("Processing Beneficiary");
                beneficiaryWorker.syncBeneficiariesFromAvniToAmrit();
            }
            if (hasTask(tasks, IntegrationTask.BeneficiaryScan)) {
                logger.info("Processing Beneficiary Scan");
                beneficiaryWorker.scanSyncStatusOfBeneficiariesFromAvniToAmrit();
            }
        } catch (Throwable e) {
            logger.error("Failed processBeneficiaryAndBeneficiaryScan", e);
            bugsnag.notify(e);
        }
    }

    private void processHousehold(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.Household)) {
                logger.info("Processing Household");
                householdWorker.syncHouseholdsFromAvniToAmrit();
            }
        } catch (Throwable e) {
            logger.error("Failed processHousehold", e);
            bugsnag.notify(e);
        }
    }

    private void processBornBirthAndCBAC(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.BornBirth)) {
                logger.info("Processing BornBirth");
                amritEnrolmentWorker.syncEnrolmentsFromAvniToAmrit(AmritEntityType.BornBirth);
            }
            if (hasTask(tasks, IntegrationTask.CBAC)) {
                logger.info("Processing CBAC");
                amritEnrolmentWorker.syncEnrolmentsFromAvniToAmrit(AmritEntityType.CBAC);
            }
        } catch (Throwable e) {
            logger.error("Failed processBornBirthAndCBAC", e);
            bugsnag.notify(e);
        }
    }

    private void processErrors(List<IntegrationTask> tasks) {
        try {
            /**
             * All our Error Records for Amrit, are stored using integrating_entity_type column,
             * hence only SyncDirection.AvniToAmrit matters.
             */
            if (hasTask(tasks, IntegrationTask.AmritErrorRecords)) {
                logger.info("Processing AmritErrorRecords");
                processErrorRecords(SyncDirection.AvniToAmrit);
            }
        } catch (Throwable e) {
            logger.error("Failed processErrors", e);
            bugsnag.notify(e);
        }
    }

    private void processErrorRecords(SyncDirection syncDirection) throws Exception {
        errorRecordsWorker.processErrors(syncDirection, false);
    }

    private boolean hasTask(List<IntegrationTask> tasks, IntegrationTask task) {
        return tasks.stream().filter(integrationTask -> integrationTask.equals(task)).findAny().orElse(null) != null;
    }

}
