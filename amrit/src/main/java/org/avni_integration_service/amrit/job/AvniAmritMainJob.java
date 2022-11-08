package org.avni_integration_service.amrit.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritAvniSessionFactory;
import org.avni_integration_service.amrit.worker.BeneficiaryWorker;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.util.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    AmritAvniSessionFactory amritAvniSessionFactory;

    @Autowired
    private AvniHttpClient avniHttpClient;

    public void execute() {
        try {
            logger.info("Starting the Exotel call sync into Avni");
            avniHttpClient.setAvniSession(amritAvniSessionFactory.createSession());
            beneficiaryWorker.syncBeneficiariesFromAvniToAmrit();
        } catch (Throwable e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        } finally {
            healthCheckService.verify(mainJobId);
        }
    }

}
