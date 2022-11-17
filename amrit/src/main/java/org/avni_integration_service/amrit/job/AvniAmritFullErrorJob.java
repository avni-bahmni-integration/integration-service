package org.avni_integration_service.amrit.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.SyncDirection;
import org.avni_integration_service.amrit.config.AmritAvniSessionFactory;
import org.avni_integration_service.amrit.worker.AmritErrorRecordWorker;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvniAmritFullErrorJob {
    private static final Logger logger = Logger.getLogger(AvniAmritFullErrorJob.class);

    @Autowired
    private AmritErrorRecordWorker amritErrorRecordWorker;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    AmritAvniSessionFactory amritAvniSessionFactory;

    @Autowired
    private AvniHttpClient avniHttpClient;

    public void execute() {
        try {
            logger.info("Starting to process the error records for call details");
            avniHttpClient.setAvniSession(amritAvniSessionFactory.createSession());
            amritErrorRecordWorker.processErrors(SyncDirection.AvniToAmrit,true);
        } catch (Exception e) {
            logger.error("Failed AvniToAmritFullErrorJob", e);
            bugsnag.notify(e);
        }
    }

}
