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
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public abstract class GeneralEncounterWorker implements ErrorRecordWorker {
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final GoonjMappingGroup goonjMappingGroup;
    private final IntegratingEntityStatusRepository integrationEntityStatusRepository;
    private static final Logger logger = Logger.getLogger(GeneralEncounterWorker.class);
    private Constants constants;

    public GeneralEncounterWorker(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  AvniGoonjErrorService avniGoonjErrorService,
                                  GoonjMappingGroup goonjMappingGroup, IntegratingEntityStatusRepository integrationEntityStatusRepository) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.goonjMappingGroup = goonjMappingGroup;
        this.integrationEntityStatusRepository = integrationEntityStatusRepository;
    }
    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
    }
    public void processEncounters() {
        while (true) {
            IntegratingEntityStatus status = integrationEntityStatusRepository.findByEntityType(AvniEntityType.GeneralEncounter.name());
            GeneralEncountersResponse response = avniEncounterRepository.getGeneralEncounters(status.getReadUptoDateTime());
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
            ;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW) //TODO @Vivek, should we retain this transactional.?
    public void processGeneralEncounter(GeneralEncounter generalEncounter, boolean updateSyncStatus) {
        if (goonjMappingGroup.isGoonjEncounterInAvni(generalEncounter.getEncounterType())) {
            logger.debug(String.format("Skipping Avni general encounter %s because it was created from Bahmni", generalEncounter.getEncounterType()));
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
            logger.error(String.format("Avni encounter 5s could not be synced to Goonj Salesforce. ", generalEncounter.getUuid()), e);
        }
    }
    protected abstract void createOrUpdateGeneralEncounter(GeneralEncounter generalEncounter, Subject subject);
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
