package org.avni_integration_service.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.util.HealthCheckService;
import org.avni_integration_service.worker.CallDetailsWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AvniPowerMainJob {

    private static final Logger logger = Logger.getLogger(AvniPowerMainJob.class);

    @Value("${healthcheck.mainJob}")
    private String mainJobId;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private CallDetailsWorker callDetailsWorker;

    public void execute() {
        try {
            logger.info("Starting the Exotel call sync into Avni");
            callDetailsWorker.fetchCallDetails();
        } catch (Throwable e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        } finally {
            healthCheckService.verify(mainJobId);
        }
    }

}
