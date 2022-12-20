package org.avni_integration_service.goonj.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.GeneralEncountersResponse;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
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

public abstract class GeneralEncounterWorker implements ErrorRecordWorker {
    public static final int SECONDS_TO_ADD = 1;
    private static final int INT_CONSTANT_ONE = 1;
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final IntegratingEntityStatusRepository integrationEntityStatusRepository;

    private final GoonjErrorType goonjErrorType;
    private final GoonjEntityType entityType;
    private final String encounterType;
    private final Logger logger;
    private final ErrorClassifier errorClassifier;
    private final IntegrationSystem integrationSystem;

    public GeneralEncounterWorker(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  AvniGoonjErrorService avniGoonjErrorService,
                                  IntegratingEntityStatusRepository integrationEntityStatusRepository,
                                  GoonjErrorType goonjErrorType, GoonjEntityType entityType, Logger logger,
                                  ErrorClassifier errorClassifier, IntegrationSystem integrationSystem) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.integrationEntityStatusRepository = integrationEntityStatusRepository;
        this.goonjErrorType = goonjErrorType;
        this.entityType = entityType;
        this.encounterType = entityType.getDbName();
        this.logger = logger;
        this.errorClassifier = errorClassifier;
        this.integrationSystem = integrationSystem;
    }

    public void processEncounters() throws Exception {
        while (true) {
            IntegratingEntityStatus status = integrationEntityStatusRepository.findByEntityType(encounterType);
            Date readUptoDateTime = getEffectiveCutoffDateTime(status);
            GeneralEncountersResponse response = avniEncounterRepository.getGeneralEncounters(readUptoDateTime, encounterType);
            GeneralEncounter[] generalEncounters = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d encounters that are newer than %s", generalEncounters.length, readUptoDateTime));
            if (generalEncounters.length == 0) break;
            for (GeneralEncounter generalEncounter : generalEncounters) {
                processGeneralEncounter(generalEncounter, true, goonjErrorType);
            }
            if (totalPages == INT_CONSTANT_ONE) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    /**
     * Add an offset to avoid syncing the last Avni encounter to Goonj
     * @param status
     * @return EffectiveCutoffDateTime
     */
    private Date getEffectiveCutoffDateTime(IntegratingEntityStatus status) {
        return new Date(status.getReadUptoDateTime().toInstant().plusSeconds(SECONDS_TO_ADD)
                .toEpochMilli());
    }

    @Override
    public void processError(String entityUuid) throws Exception {
        GeneralEncounter generalEncounter = avniEncounterRepository.getGeneralEncounter(entityUuid);
        if (generalEncounter == null) {
            String message = String.format("GeneralEncounter has been deleted now: %s", entityUuid);
            logger.warn(message);
            avniGoonjErrorService.errorOccurred(entityUuid, GoonjErrorType.EntityIsDeleted, AvniEntityType.GeneralEncounter, message);
            return;
        }

        processGeneralEncounter(generalEncounter, false, goonjErrorType);
    }

    public void processGeneralEncounter(GeneralEncounter generalEncounter, boolean updateSyncStatus, GoonjErrorType goonjErrorType) throws Exception {
        removeIgnoredObservations(generalEncounter);
        logger.debug(String.format("Processing avni general encounter %s", generalEncounter.getUuid()));

        if (shouldFilterEncounter(generalEncounter)) {
            logger.warn(String.format("General encounter should be filtered out: %s", generalEncounter.getUuid()));
            updateErrorRecordAndSyncStatus(generalEncounter, updateSyncStatus, generalEncounter.getUuid());
            return;
        }

        var subject = avniSubjectRepository.getSubject(generalEncounter.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        if (subject.getVoided()) {
            logger.debug(String.format("Avni subject is voided. Skipping. %s", subject.getUuid()));
            updateErrorRecordAndSyncStatus(generalEncounter, updateSyncStatus, generalEncounter.getUuid());
            return;
        }

        try {
            createOrUpdateGeneralEncounter(generalEncounter, subject);
            updateErrorRecordAndSyncStatus(generalEncounter, updateSyncStatus, generalEncounter.getUuid());
        } catch (Exception e) {
            handleError(generalEncounter, e, updateSyncStatus, goonjErrorType);
        }
    }

    protected void handleError(GeneralEncounter generalEncounter, Exception exception,
                               boolean updateSyncStatus, GoonjErrorType goonjErrorType) throws Exception {
        logger.error(String.format("Avni encounter %s could not be synced to Goonj Salesforce. ", generalEncounter.getUuid()), exception);
        ErrorType classifiedErrorType = errorClassifier.classify(integrationSystem, exception);
        if(classifiedErrorType == null) {
            throw exception;
        }
        createOrUpdateErrorRecordAndSyncStatus(generalEncounter, updateSyncStatus, generalEncounter.getUuid(),
                goonjErrorType, exception.getLocalizedMessage());
    }

    protected abstract void createOrUpdateGeneralEncounter(GeneralEncounter generalEncounter, Subject subject);

    protected void syncEncounterToGoonj(Subject subject, GeneralEncounter generalEncounter, GoonjBaseRepository repository, String encounterTypeId) {
        HashMap<String, Object>[] response = repository.createEvent(subject, generalEncounter);
        logger.debug(String.format("%s %s synced successfully. ", encounterTypeId, response[0].get(encounterTypeId)));
    }

    private void removeIgnoredObservations(GeneralEncounter generalEncounter) {
        var observations = generalEncounter.getObservations();
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        generalEncounter.setObservations(observations);
    }

    private boolean shouldFilterEncounter(GeneralEncounter generalEncounter) {
        return !generalEncounter.isCompleted() || generalEncounter.getVoided();
    }

    private void updateErrorRecordAndSyncStatus(GeneralEncounter generalEncounter, boolean updateSyncStatus, String sid) {
        avniGoonjErrorService.successfullyProcessed(sid, entityType);
        updateSyncStatus(generalEncounter, updateSyncStatus);
    }

    private void createOrUpdateErrorRecordAndSyncStatus(GeneralEncounter generalEncounter, boolean updateSyncStatus, String sid, GoonjErrorType goonjErrorType, String errorMsg) {
        avniGoonjErrorService.errorOccurred(sid, goonjErrorType, entityType, errorMsg);
        updateSyncStatus(generalEncounter, updateSyncStatus);
    }

    private void updateSyncStatus(GeneralEncounter generalEncounter, boolean updateSyncStatus) {
        if (updateSyncStatus) {
            updateReadUptoDateTime(generalEncounter);
        }
    }

    private void updateReadUptoDateTime(GeneralEncounter generalEncounter) {
        IntegratingEntityStatus intEnt = integrationEntityStatusRepository.findByEntityType(encounterType);
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate(generalEncounter.getLastModifiedDateTime().toString()));
        integrationEntityStatusRepository.save(intEnt);
    }
}
