package org.avni_integration_service.amrit.worker;

import org.avni_integration_service.integration_data.domain.error.ErrorRecord;

public interface ErrorRecordWorker {
    void processError(ErrorRecord errorRecord);
}
