package org.avni_integration_service.goonj.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.GeneralEncountersResponse;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.GoonjMappingGroup;
import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public abstract class GeneralEncounterWorker implements ErrorRecordWorker {
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final GoonjMappingGroup goonjMappingGroup;
    private final IntegratingEntityStatusRepository integrationEntityStatusRepository;
    private final String encounterType;
    private final Logger logger;
    private Constants constants;

    public GeneralEncounterWorker(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  AvniGoonjErrorService avniGoonjErrorService,
                                  GoonjMappingGroup goonjMappingGroup, IntegratingEntityStatusRepository integrationEntityStatusRepository,
                                  String encounterType, Logger logger) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.goonjMappingGroup = goonjMappingGroup;
        this.integrationEntityStatusRepository = integrationEntityStatusRepository;
        this.encounterType = encounterType;
        this.logger = logger;
    }
    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
    }
    public void processEncounters() {
        while (true) {
            IntegratingEntityStatus status = integrationEntityStatusRepository.findByEntityType(AvniEntityType.GeneralEncounter.name());
            GeneralEncountersResponse response = avniEncounterRepository.getGeneralEncounters(status.getReadUptoDateTime(), encounterType);
            GeneralEncounter[] generalEncounters = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d encounters that are newer than %s", generalEncounters.length, status.getReadUptoDateTime()));
            if (generalEncounters.length == 0) break;
            for (GeneralEncounter generalEncounter : generalEncounters) {
                processGeneralEncounter(generalEncounter, true);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }
    @Override
    public void processError(String entityUuid) {
        GeneralEncounter generalEncounter = avniEncounterRepository.getGeneralEncounter(entityUuid);
        if (generalEncounter == null) {
            logger.warn(String.format("GeneralEncounter has been deleted now: %s", entityUuid));
            avniGoonjErrorService.errorOccurred(entityUuid, GoonjErrorType.EntityIsDeleted, AvniEntityType.GeneralEncounter);
            return;
        }

        processGeneralEncounter(generalEncounter, false);
    }
    public void processGeneralEncounter(GeneralEncounter generalEncounter, boolean updateSyncStatus) {
        if (goonjMappingGroup.isGoonjEncounterInAvni(generalEncounter.getEncounterType())) {
            logger.debug(String.format("Skipping Avni general encounter %s because it was created from Goonj. ", generalEncounter.getEncounterType()));
            updateSyncStatus(generalEncounter, updateSyncStatus);
            return;
        }
        
        removeIgnoredObservations(generalEncounter);
        logger.debug(String.format("Processing avni general encounter %s", generalEncounter.getUuid()));

        if (shouldFilterEncounter(generalEncounter)) {
            logger.warn(String.format("General encounter should be filtered out: %s", generalEncounter.getUuid()));
            updateSyncStatus(generalEncounter, updateSyncStatus);
            return;
        }

        //TODO Should we stop sync back of counters if the Goonj Demand is voided.?
        var subject = avniSubjectRepository.getSubject(generalEncounter.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        if (subject.getVoided()) {
            logger.debug(String.format("Avni subject is voided. Skipping. %s", subject.getUuid()));
            updateSyncStatus(generalEncounter, updateSyncStatus);
            return;
        }

        try {
            createOrUpdateGeneralEncounter(generalEncounter, subject);
            updateSyncStatus(generalEncounter, updateSyncStatus);
            return;
        } catch (Exception e) {
            logger.error(String.format("Avni encounter %s could not be synced to Goonj Salesforce. ", generalEncounter.getUuid()), e);
            throw e; //Throw exception, so that we stop at the failed encounter and not proceed to the next one
        }
    }
    protected abstract void createOrUpdateGeneralEncounter(GeneralEncounter generalEncounter, Subject subject);
    protected void syncEncounterToGoonj(Subject subject, GeneralEncounter generalEncounter, GoonjBaseRepository repository, String encounterTypeId) {
        HashMap<String, Object>[] response = repository.createEvent(subject, generalEncounter);
        if(repository.wasEventCreatedSuccessfully(response)) {
            logger.debug(String.format("%s %s synced successfully. ", encounterTypeId, response[0].get(encounterTypeId)));
        } else {
            logger.error(String.format("Failed to sync %s with uuid %s ", encounterTypeId, generalEncounter.getUuid()));
        }
    }
    private void removeIgnoredObservations(GeneralEncounter generalEncounter) {
        var observations = generalEncounter.getObservations();
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        generalEncounter.setObservations(observations);
    }
    private boolean shouldFilterEncounter(GeneralEncounter generalEncounter) {
        return !generalEncounter.isCompleted();
    }
    private void updateSyncStatus(GeneralEncounter generalEncounter, boolean updateSyncStatus) {
        if (updateSyncStatus) {
            updateReadUptoDateTime(generalEncounter);
        }
    }
    void updateReadUptoDateTime(GeneralEncounter generalEncounter) {
        IntegratingEntityStatus intEnt = integrationEntityStatusRepository.findByEntityType(AvniEntityType.GeneralEncounter.name());
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate(generalEncounter.getLastModifiedDateTime().toString()));
        integrationEntityStatusRepository.save(intEnt);
    }
}
