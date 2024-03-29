package org.bahmni_avni_integration.scheduler;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.SyncDirection;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.worker.ErrorRecordsWorker;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class FullErrorJob implements Job {
    private static final Logger logger = Logger.getLogger(FullErrorJob.class);

    @Autowired
    private ConstantsRepository constantsRepository;

    @Autowired
    private ErrorRecordsWorker errorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    public void execute(JobExecutionContext context) {
        logger.info(String.format("Job ** {%s} ** fired @ {%s}", context.getJobDetail().getKey().getName(), context.getFireTime()));
        try {
            Constants allConstants = constantsRepository.findAllConstants();
            errorRecordsWorker.cacheRunImmutables(allConstants);
            errorRecordsWorker.process(SyncDirection.BahmniToAvni, true);
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
        logger.info(String.format("Next job scheduled @ {%s}", context.getNextFireTime()));
    }
}
