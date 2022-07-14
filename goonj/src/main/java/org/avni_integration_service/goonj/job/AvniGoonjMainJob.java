package org.avni_integration_service.goonj.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.goonj.worker.AvniGoonjErrorRecordsWorker;
import org.avni_integration_service.goonj.worker.goonj.DemandWorker;
import org.avni_integration_service.goonj.worker.goonj.DispatchWorker;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.util.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvniGoonjMainJob {
    private static final Logger logger = Logger.getLogger(AvniGoonjMainJob.class);

    @Value("${healthcheck.mainJob}")
    private String mainJobId;

    @Autowired
    private DemandWorker demandWorker;

    @Autowired
    private DispatchWorker dispatchWorker;

    @Autowired
    private ConstantsRepository constantsRepository;
    @Value("${goonj.app.tasks}")
    private String tasks;
    @Value("${goonj.app.first.run}")
    private boolean isFirstRun;

    @Autowired
    private AvniGoonjErrorRecordsWorker errorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private HealthCheckService healthCheckService;

    public void execute() {
        try {
            logger.info("Inside  Goonj execute");
            List<IntegrationTask> tasks = IntegrationTask.getTasks(this.tasks);
            Constants allConstants = constantsRepository.findAllConstants();

            if (hasTask(tasks, IntegrationTask.GoonjDemand)) {
                logger.info("Processing GoonjDemand");
                getDemandWorker(allConstants).process();
                getDemandWorker(allConstants).processDeletions();
            }
            if (hasTask(tasks, IntegrationTask.GoonjDispatch)) {
                logger.info("Processing GoonjDispatch");
                getDispatchWorker(allConstants).process();
                getDispatchWorker(allConstants).processDeletions();
            }
            if (hasTask(tasks, IntegrationTask.AvniErrorRecords)) {
                logger.info("Processing AvniErrorRecords");
                processErrorRecords(allConstants, SyncDirection.AvniToGoonj);
            }
            if (hasTask(tasks, IntegrationTask.GoonjErrorRecords)) {
                logger.info("Processing GoonjErrorRecords");
                processErrorRecords(allConstants, SyncDirection.GoonjToAvni);
            }
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        } catch (Throwable t) {
            logger.error("Failed", t);
            bugsnag.notify(t);
        } finally {
            healthCheckService.verify(mainJobId);
        }
    }

    private void processErrorRecords(Constants allConstants, SyncDirection syncDirection) {
        errorRecordsWorker.process(syncDirection, false);
    }

    public DemandWorker getDemandWorker(Constants constants) {
        demandWorker.cacheRunImmutables(constants);
        return demandWorker;
    }

    public DispatchWorker getDispatchWorker(Constants constants) {
        dispatchWorker.cacheRunImmutables(constants);
        return dispatchWorker;
    }

    private boolean hasTask(List<IntegrationTask> tasks, IntegrationTask task) {
        return tasks.stream().filter(integrationTask -> integrationTask.equals(task)).findAny().orElse(null) != null;
    }
}
