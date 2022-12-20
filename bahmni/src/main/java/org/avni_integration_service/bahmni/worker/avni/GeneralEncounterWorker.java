package org.avni_integration_service.bahmni.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.GeneralEncountersResponse;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.bahmni.BahmniErrorType;
import org.avni_integration_service.bahmni.Names;
import org.avni_integration_service.bahmni.SubjectToPatientMetaData;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.bahmni.service.AvniBahmniErrorService;
import org.avni_integration_service.bahmni.service.AvniEncounterService;
import org.avni_integration_service.bahmni.service.AvniEntityStatusService;
import org.avni_integration_service.bahmni.service.MappingMetaDataService;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GeneralEncounterWorker implements ErrorRecordWorker {
    private final IntegratingEntityStatusRepository integrationEntityStatusRepository;
    private final AvniEncounterRepository avniEncounterRepository;
    private final MappingMetaDataService mappingMetaDataService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniEncounterService encounterService;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;

    private static final Logger logger = Logger.getLogger(GeneralEncounterWorker.class);
    private final AvniEntityStatusService avniEntityStatusService;
    private Constants constants;
    private SubjectToPatientMetaData metaData;
    private final AvniBahmniErrorService avniBahmniErrorService;

    public GeneralEncounterWorker(IntegratingEntityStatusRepository integrationEntityStatusRepository,
                                  MappingMetaDataService mappingMetaDataService,
                                  AvniEncounterRepository avniEncounterRepository,
                                  AvniSubjectRepository avniSubjectRepository,
                                  AvniEncounterService encounterService,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  AvniEntityStatusService avniEntityStatusService,
                                  AvniBahmniErrorService avniBahmniErrorService) {
        this.integrationEntityStatusRepository = integrationEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.encounterService = encounterService;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniEntityStatusService = avniEntityStatusService;
        this.avniBahmniErrorService = avniBahmniErrorService;
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

    private void removeIgnoredObservations(GeneralEncounter generalEncounter) {
        var observations = generalEncounter.getObservations();
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        generalEncounter.setObservations(observations);
    }

    private void updateSyncStatus(GeneralEncounter generalEncounter, boolean updateSyncStatus) {
        if (updateSyncStatus)
            avniEntityStatusService.saveEntityStatus(generalEncounter);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processGeneralEncounter(GeneralEncounter generalEncounter, boolean updateSyncStatus) {
        if (mappingMetaDataService.isBahmniEncounterInAvni(generalEncounter.getEncounterType()) || generalEncounter.getEncounterType().equals(Names.AvniPatientRegistrationEncounter)) {
            logger.debug(String.format("Skipping Avni general encounter %s because it was created from Bahmni", generalEncounter.getEncounterType()));
            updateSyncStatus(generalEncounter, updateSyncStatus);
            return;
        }

        if (avniBahmniErrorService.hasAvniMultipleSubjectsError(generalEncounter.getSubjectId())) {
            logger.error(String.format("Skipping Avni general encounter %s because of multiple subjects with same id error", generalEncounter.getUuid()));
            avniBahmniErrorService.errorOccurred(generalEncounter, BahmniErrorType.MultipleSubjectsWithId);
            updateSyncStatus(generalEncounter, updateSyncStatus);
            return;
        }
        removeIgnoredObservations(generalEncounter);
        logger.debug(String.format("Processing avni general encounter %s", generalEncounter.getUuid()));

        if (encounterService.shouldFilterEncounter(generalEncounter)) {
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
        Pair<OpenMRSPatient, OpenMRSFullEncounter> patientEncounter = encounterService.findCommunityEncounter(generalEncounter, subject, constants, metaData);
        var patient = patientEncounter.getValue0();
        var encounter = patientEncounter.getValue1();

        if (patient != null && encounter == null) {
            encounterService.createCommunityEncounter(generalEncounter, patient, constants);
        } else if (patient != null && encounter != null) {
            encounterService.updateCommunityEncounter(encounter, generalEncounter, constants);
        } else if (patient == null && encounter == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData.avniIdentifierConcept())));
            encounterService.processPatientNotFound(generalEncounter);
        }

        updateSyncStatus(generalEncounter, updateSyncStatus);
    }

    @Override
    public void processError(String entityUuid) {
        GeneralEncounter generalEncounter = avniEncounterRepository.getGeneralEncounter(entityUuid);
        if (generalEncounter == null) {
            logger.warn(String.format("GeneralEncounter has been deleted now: %s", entityUuid));
            avniBahmniErrorService.errorOccurred(entityUuid, BahmniErrorType.EntityIsDeleted, AvniEntityType.GeneralEncounter);
            return;
        }

        processGeneralEncounter(generalEncounter, false);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}
