package org.avni_integration_service.bahmni.worker;

public interface ErrorRecordWorker {
    void processError(String entityUuid);
}
