package org.bahmni_avni_integration.worker;

public interface ErrorRecordWorker {
    void processError(String entityUuid);
}
