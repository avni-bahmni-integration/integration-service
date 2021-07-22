package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.GeneralEncountersResponse;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniIgnoredConceptsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.AvniEncounterService;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.ErrorService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.worker.ErrorRecordWorker;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;

@Component
public class GeneralEncounterWorker implements ErrorRecordWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniEncounterRepository avniEncounterRepository;
    private final MappingMetaDataService mappingMetaDataService;
    private final AvniSubjectRepository avniSubjectRepository;
    private AvniEncounterService encounterService;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;

    private static final Logger logger = Logger.getLogger(GeneralEncounterWorker.class);
    private final EntityStatusService entityStatusService;
    private Constants constants;
    private SubjectToPatientMetaData metaData;
    private final ErrorService errorService;

    public GeneralEncounterWorker(AvniEntityStatusRepository avniEntityStatusRepository,
                                  MappingMetaDataService mappingMetaDataService,
                                  AvniEncounterRepository avniEncounterRepository,
                                  AvniSubjectRepository avniSubjectRepository,
                                  AvniEncounterService encounterService,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  EntityStatusService entityStatusService,
                                  ErrorService errorService) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.encounterService = encounterService;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.entityStatusService = entityStatusService;
        this.errorService = errorService;
    }

    public void processEncounters() {
        while (true) {
            AvniEntityStatus subjectStatus = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.GeneralEncounter);
            GeneralEncountersResponse response = avniEncounterRepository.getGeneralEncounters(status.getReadUpto());
            GeneralEncounter[] generalEncounters = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d encounters that are newer than %s", generalEncounters.length, status.getReadUpto()));
            generalEncounters = Arrays.stream(generalEncounters).filter(generalEncounter -> generalEncounter.getLastModifiedDate().before(subjectStatus.getReadUpto())).toArray(GeneralEncounter[]::new);
            logger.info(String.format("Found %d encounters that are newer than %s and before %s", generalEncounters.length, status.getReadUpto(), subjectStatus.getReadUpto()));
            if (generalEncounters.length == 0) break;
            for (GeneralEncounter generalEncounter : generalEncounters) {
                processGeneralEncounter(generalEncounter);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            };
        }
    }

    private void removeIgnoredObservations(GeneralEncounter generalEncounter) {
        var observations = (LinkedHashMap<String, Object>) generalEncounter.get("observations");
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        generalEncounter.set("observations", observations);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processGeneralEncounter(GeneralEncounter generalEncounter) {
        if (errorService.hasAvniMultipleSubjectsError(generalEncounter.getSubjectId())) {
            logger.error(String.format("Skipping Avni general encounter %s because of multiple subjects with same id error", generalEncounter.getUuid()));
            entityStatusService.saveEntityStatus(generalEncounter);
            return;
        }
        removeIgnoredObservations(generalEncounter);
        logger.debug(String.format("Processing avni general encounter %s", generalEncounter.getUuid()));

        if (encounterService.shouldFilterEncounter(generalEncounter)) {
            logger.warn(String.format("General encounter should be filtered out: %s", generalEncounter.getUuid()));
            entityStatusService.saveEntityStatus(generalEncounter);
            return;
        }

        var subject = avniSubjectRepository.getSubject(generalEncounter.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        if (subject.getVoided()) {
            logger.debug(String.format("Avni subject is voided. Skipping. %s", subject.getUuid()));
            entityStatusService.saveEntityStatus(generalEncounter);
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
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData)));
            encounterService.processPatientNotFound(generalEncounter);
        }

        entityStatusService.saveEntityStatus(generalEncounter);
    }

    @Override
    public void processError(String entityUuid) {
        GeneralEncounter generalEncounter = avniEncounterRepository.getGeneralEncounter(entityUuid);
        if (generalEncounter == null) {
            logger.warn(String.format("GeneralEncounter has been deleted now: %s", entityUuid));
            errorService.errorOccurred(entityUuid, ErrorType.EntityIsDeleted, AvniEntityType.GeneralEncounter);
            return;
        }

        processGeneralEncounter(generalEncounter);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}
