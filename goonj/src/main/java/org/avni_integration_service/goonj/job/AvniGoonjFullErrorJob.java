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
    private AvniGoonjErrorRecordsWorker avniGoonjErrorRecordsWorker;

    @Autowired
    private Bugsnag bugsnag;

    public void execute() {
        try {
            /**
             * All our Error Records for Goonj, i.e. Demand, Dispatch, Distro, DispatchReceipt and Activity
             * are stored using integrating_entity_type column, hence only SyncDirection.GoonjToAvni matters
             * and not the other-way(SyncDirection.AvniToGoonj) around.
             */
            avniGoonjErrorRecordsWorker.process(SyncDirection.GoonjToAvni, true);
        } catch (Exception e) {
            logger.error("Failed", e);
            bugsnag.notify(e);
        }
    }
}
