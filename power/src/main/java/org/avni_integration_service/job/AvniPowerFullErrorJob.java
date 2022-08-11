package org.avni_integration_service.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.worker.PowerErrorRecordWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvniPowerFullErrorJob {
    private static final Logger logger = Logger.getLogger(AvniPowerFullErrorJob.class);

    @Autowired
    private PowerErrorRecordWorker powerErrorRecordWorker;

    @Autowired
    private Bugsnag bugsnag;

    public void execute() {
        try {
            logger.info("Starting to process the error records for call details");
            powerErrorRecordWorker.processErrors();
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
    }

}
