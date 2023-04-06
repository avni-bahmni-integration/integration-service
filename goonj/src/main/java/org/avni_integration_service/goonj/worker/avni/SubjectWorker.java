package org.avni_integration_service.goonj.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.domain.SubjectsResponse;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;

import java.util.Date;
import java.util.HashMap;

public abstract class SubjectWorker implements ErrorRecordWorker {
    public static final int SECONDS_TO_ADD = 1;
    private static final int INT_CONSTANT_ONE = 1;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final IntegratingEntityStatusRepository integrationEntityStatusRepository;

    private final GoonjErrorType goonjErrorType;
    private final GoonjEntityType entityType;
    private final String subjectType;
    private final Logger logger;
    private final ErrorClassifier errorClassifier;
    private final IntegrationSystem integrationSystem;

    public SubjectWorker(AvniSubjectRepository avniSubjectRepository,
                         AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                         AvniGoonjErrorService avniGoonjErrorService,
                         IntegratingEntityStatusRepository integrationEntityStatusRepository,
                         GoonjErrorType goonjErrorType, GoonjEntityType entityType, Logger logger,
                         ErrorClassifier errorClassifier, IntegrationSystem integrationSystem) {
        this.avniSubjectRepository = avniSubjectRepository;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.integrationEntityStatusRepository = integrationEntityStatusRepository;
        this.goonjErrorType = goonjErrorType;
        this.entityType = entityType;
        this.subjectType = entityType.getDbName();
        this.logger = logger;
        this.errorClassifier = errorClassifier;
        this.integrationSystem = integrationSystem;
    }

    public void processSubjects() throws Exception {
        while (true) {
            IntegratingEntityStatus status = integrationEntityStatusRepository.findByEntityType(subjectType);
            Date readUptoDateTime = getEffectiveCutoffDateTime(status);
            SubjectsResponse response = avniSubjectRepository.getSubjects(readUptoDateTime, subjectType);
            Subject[] subjects = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, readUptoDateTime));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                processSubject(subject, true, goonjErrorType);
            }
            if (totalPages == INT_CONSTANT_ONE) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    /**
     * Add an offset to avoid syncing the last Avni subject to Goonj
     * @param status
     * @return EffectiveCutoffDateTime
     */
    private Date getEffectiveCutoffDateTime(IntegratingEntityStatus status) {
        return new Date(status.getReadUptoDateTime().toInstant().plusSeconds(SECONDS_TO_ADD)
                .toEpochMilli());
    }

    @Override
    public void processError(String entityUuid) throws Exception {
        Subject subject = avniSubjectRepository.getSubject(entityUuid);
        if (subject == null) {
            String message = String.format("Subject has been deleted now: %s", entityUuid);
            logger.warn(message);
            avniGoonjErrorService.errorOccurred(entityUuid, GoonjErrorType.EntityIsDeleted, AvniEntityType.Subject, message);
            return;
        }

        processSubject(subject, false, goonjErrorType);
    }

    public void processSubject(Subject subject, boolean updateSyncStatus, GoonjErrorType goonjErrorType) throws Exception {
        removeIgnoredObservations(subject);
        logger.debug(String.format("Processing avni subject %s", subject.getUuid()));

        if (shouldFilterSubject(subject)) {
            logger.warn(String.format("Subject should be filtered out: %s", subject.getUuid()));
            updateErrorRecordAndSyncStatus(subject, updateSyncStatus, subject.getUuid());
            return;
        }

        try {
            createSubject(subject);
            updateErrorRecordAndSyncStatus(subject, updateSyncStatus, subject.getUuid());
        } catch (Exception e) {
            handleError(subject, e, updateSyncStatus, goonjErrorType);
        }
    }

    protected void handleError(Subject subject, Exception exception,
                               boolean updateSyncStatus, GoonjErrorType goonjErrorType) throws Exception {
        logger.error(String.format("Avni subject %s could not be synced to Goonj Salesforce. ", subject.getUuid()), exception);
        ErrorType classifiedErrorType = errorClassifier.classify(integrationSystem, exception);
        if(classifiedErrorType == null) {
            throw exception;
        }
        createOrUpdateErrorRecordAndSyncStatus(subject, updateSyncStatus, subject.getUuid(),
                goonjErrorType, exception.getLocalizedMessage());
    }

    protected abstract void createSubject(Subject subject);

    protected void syncSubjectToGoonj(Subject subject, GoonjBaseRepository repository, String subjectTypeId) {
        HashMap<String, Object>[] response = repository.createEvent(subject);
        logger.debug(String.format("%s %s synced successfully. ", subjectTypeId, response[0].get(subjectTypeId)));
    }

    private void removeIgnoredObservations(Subject subject) {
        var observations = subject.getObservations();
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        subject.setObservations(observations);
    }

    private boolean shouldFilterSubject(Subject subject) {
        return !subject.isCompleted() || subject.getVoided();
    }

    private void updateErrorRecordAndSyncStatus(Subject subject, boolean updateSyncStatus, String sid) {
        avniGoonjErrorService.successfullyProcessed(sid, entityType);
        updateSyncStatus(subject, updateSyncStatus);
    }

    private void createOrUpdateErrorRecordAndSyncStatus(Subject subject, boolean updateSyncStatus, String sid, GoonjErrorType goonjErrorType, String errorMsg) {
        avniGoonjErrorService.errorOccurred(sid, goonjErrorType, entityType, errorMsg);
        updateSyncStatus(subject, updateSyncStatus);
    }

    private void updateSyncStatus(Subject subject, boolean updateSyncStatus) {
        if (updateSyncStatus) {
            updateReadUptoDateTime(subject);
        }
    }

    private void updateReadUptoDateTime(Subject subject) {
        IntegratingEntityStatus intEnt = integrationEntityStatusRepository.findByEntityType(subjectType);
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate(subject.getLastModifiedDateTime().toString()));
        integrationEntityStatusRepository.save(intEnt);
    }
}
