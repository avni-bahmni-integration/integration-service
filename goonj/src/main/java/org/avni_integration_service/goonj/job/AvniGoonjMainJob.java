package org.avni_integration_service.goonj.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.client.AvniSession;
import org.avni_integration_service.goonj.worker.AvniGoonjErrorRecordsWorker;
import org.avni_integration_service.goonj.worker.avni.ActivityWorker;
import org.avni_integration_service.goonj.worker.avni.DispatchReceiptWorker;
import org.avni_integration_service.goonj.worker.avni.DistributionWorker;
import org.avni_integration_service.goonj.worker.goonj.DemandWorker;
import org.avni_integration_service.goonj.worker.goonj.DispatchWorker;
import org.avni_integration_service.goonj.worker.goonj.InventoryWorker;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.util.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvniGoonjMainJob {
    private static final Logger logger = Logger.getLogger(AvniGoonjMainJob.class);

    private static final String HEALTHCHECK_SLUG = "goonj";

    @Autowired
    private DemandWorker demandWorker;

    @Autowired
    private DispatchWorker dispatchWorker;

    @Autowired
    private DispatchReceiptWorker dispatchReceiptWorker;

    @Autowired
    private DistributionWorker distributionWorker;

    @Autowired
    private ActivityWorker activityWorker;

    @Autowired
    private InventoryWorker inventoryWorker;

    @Autowired
    private ConstantsRepository constantsRepository;

    @Value("${goonj.app.tasks}")
    private String tasks;

    @Autowired
    private AvniGoonjErrorRecordsWorker errorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private AvniHttpClient avniHttpClient;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    @Qualifier("GoonjAvniSession")
    private AvniSession goonjAvniSession;

    public void execute() {
        try {
            logger.info("Executing Goonj Main Job");
            avniHttpClient.setAvniSession(goonjAvniSession);

            List<IntegrationTask> tasks = IntegrationTask.getTasks(this.tasks);
            processDemandAndDispatch(tasks);
            processActivity(tasks);
            processDispatchReceiptAndDistribution(tasks);
            processInventory(tasks);
            processErrors(tasks);
            healthCheckService.success(HEALTHCHECK_SLUG);
        } catch (Throwable e) {
            healthCheckService.failure(HEALTHCHECK_SLUG);
            logger.error("Failed AvniGoonjMainJob", e);
            bugsnag.notify(e);
        }
    }

    private void processDemandAndDispatch(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.GoonjDemand)) {
                logger.info("Processing GoonjDemand");
                demandWorker.process();
                /*
                  We are triggering deletion tagged along with Demand creations, as the Goonj System sends
                  the Deleted Demands info as part of the same getDemands API, but as a separate list,
                  without any TimeStamp and other minimal information details required to make an Update Subject as Voided call.
                  Therefore, we invoke the Delete API for subject using DemandId as externalId to mark a Demand as Voided.
                 */
                demandWorker.processDeletions();
            }
            if (hasTask(tasks, IntegrationTask.GoonjDispatch)) {
                logger.info("Processing GoonjDispatch");
                dispatchWorker.process();
                /*
                  We are triggering deletion tagged along with DispatchStatus creations, as the Goonj System sends
                  the Deleted DispatchStatuses info as part of the same getDispatchStatus API, but as a separate list,
                  without any TimeStamp and other minimal information details required to make an Update DispatchStatus as Voided call.
                  Therefore, we invoke the Delete API for DispatchStatus using DispatchStatusId as externalId to mark a DispatchStatus as Voided.
                 */
                dispatchWorker.processDeletions();
                // Todo: Dispatch line items to  be deleted!
//                dispatchWorker.processDispatchLineItemDeletions();
            }
        } catch (Throwable e) {
            logger.error("Failed processDemandAndDispatch", e);
            bugsnag.notify(e);
        }
    }

    private void processActivity(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.AvniActivity)) {
                logger.info("Processing AvniActivity");
                activityWorker.process();
            }
        } catch (Throwable e) {
            logger.error("Failed processActivity", e);
            bugsnag.notify(e);
        }
    }

    private void processDispatchReceiptAndDistribution(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.AvniDispatchReceipt)) {
                logger.info("Processing AvniDispatchReceipt");
                dispatchReceiptWorker.process();
            }
            if (hasTask(tasks, IntegrationTask.AvniDistribution)) {
                logger.info("Processing AvniDistribution");
                distributionWorker.process();
            }
        } catch (Throwable e) {
            logger.error("Failed processDispatchReceiptAndDistribution", e);
            bugsnag.notify(e);
        }
    }

    private void processInventory(List<IntegrationTask> tasks) {
        try {
            if (hasTask(tasks, IntegrationTask.GoonjInventory)) {
                logger.info("Processing GoonjInventory");
                inventoryWorker.process();
                inventoryWorker.processDeletions();
            }
        } catch (Throwable e) {
            logger.error("Failed processInventory", e);
            bugsnag.notify(e);
        }
    }

    private void processErrors(List<IntegrationTask> tasks) {
        try {
            /**
             * All our Error Records for Goonj, i.e. Demand, Dispatch, Distro, DispatchReceipt and Activity
             * are stored using integrating_entity_type column, hence only SyncDirection.GoonjToAvni matters
             * and not the other-way(SyncDirection.AvniToGoonj) around.
             */
            if (hasTask(tasks, IntegrationTask.GoonjErrorRecords)) {
                logger.info("Processing GoonjErrorRecords");
                processErrorRecords(SyncDirection.GoonjToAvni);
            }
        } catch (Throwable e) {
            logger.error("Failed processErrors", e);
            bugsnag.notify(e);
        }
    }

    private void processErrorRecords(SyncDirection syncDirection) throws Exception {
        errorRecordsWorker.process(syncDirection, false);
    }

    private boolean hasTask(List<IntegrationTask> tasks, IntegrationTask task) {
        return tasks.stream().filter(integrationTask -> integrationTask.equals(task)).findAny().orElse(null) != null;
    }
}
