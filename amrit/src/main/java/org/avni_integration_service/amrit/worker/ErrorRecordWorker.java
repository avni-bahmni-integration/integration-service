package org.avni_integration_service.amrit.worker;

import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;

import java.util.Date;

public interface ErrorRecordWorker {
    long SECONDS_TO_ADD = 1l;

    /**
     * Add an offset to avoid syncing the last Avni encounter to Goonj
     * @param status
     * @return EffectiveCutoffDateTime
     */
    default Date getEffectiveCutoffDateTime(IntegratingEntityStatus status) {
        return new Date(status.getReadUptoDateTime().toInstant().plusSeconds(ErrorRecordWorker.SECONDS_TO_ADD)
                .toEpochMilli());
    }

    void processError(ErrorRecord errorRecord);
}
