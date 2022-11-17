package org.avni_integration_service.amrit.worker;

public interface ErrorRecordWorker {
    void processError(String entityUuid) throws Exception;
}
