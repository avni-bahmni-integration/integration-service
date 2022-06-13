package org.avni_integration_service.goonj.job;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.goonj.worker.AvniGoonjErrorRecordsWorker;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvniGoonjFullErrorJob {
    private static final Logger logger = Logger.getLogger(AvniGoonjFullErrorJob.class);

    @Autowired
    private ConstantsRepository constantsRepository;

    @Autowired
    private AvniGoonjErrorRecordsWorker avniGoonjErrorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    public void execute() {
        try {
            Constants allConstants = constantsRepository.findAllConstants();
            avniGoonjErrorRecordsWorker.cacheRunImmutables(allConstants);
            avniGoonjErrorRecordsWorker.process(SyncDirection.GoonjToAvni, true);
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
    }
}
