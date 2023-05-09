package org.avni_integration_service.bahmni.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.bahmni.client.BahmniAvniSessionFactory;
import org.avni_integration_service.bahmni.worker.AvniBahmniErrorRecordsWorker;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvniBahmniFullErrorJob {
    private static final Logger logger = Logger.getLogger(AvniBahmniFullErrorJob.class);

    @Autowired
    private ConstantsRepository constantsRepository;

    @Autowired
    private AvniBahmniErrorRecordsWorker avniBahmniErrorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private AvniHttpClient avniHttpClient;

    @Autowired
    private BahmniAvniSessionFactory bahmniAvniSessionFactory;

    public void execute() {
        try {
            avniHttpClient.setAvniSession(bahmniAvniSessionFactory.createSession());
            Constants allConstants = constantsRepository.findAllConstants();
            avniBahmniErrorRecordsWorker.cacheRunImmutables(allConstants);
            avniBahmniErrorRecordsWorker.process(SyncDirection.BahmniToAvni, true);
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
    }
}
