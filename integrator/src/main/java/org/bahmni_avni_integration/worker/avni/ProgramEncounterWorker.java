package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.avni.ProgramEncountersResponse;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniIgnoredConceptsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniProgramEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.ErrorService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.ProgramEncounterService;
import org.bahmni_avni_integration.worker.ErrorRecordWorker;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

@Component
public class ProgramEncounterWorker implements ErrorRecordWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniProgramEncounterRepository avniProgramEncounterRepository;
    private final MappingMetaDataService mappingMetaDataService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final ProgramEncounterService programEncounterService;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;

    private static final Logger logger = Logger.getLogger(ProgramEncounterWorker.class);
    private final EntityStatusService entityStatusService;
    private Constants constants;
    private SubjectToPatientMetaData metaData;
    private final ErrorService errorService;

    public ProgramEncounterWorker(AvniEntityStatusRepository avniEntityStatusRepository,
                                  MappingMetaDataService mappingMetaDataService,
                                  AvniProgramEncounterRepository avniProgramEncounterRepository,
                                  AvniSubjectRepository avniSubjectRepository,
                                  ProgramEncounterService programEncounterService,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  EntityStatusService entityStatusService,
                                  ErrorService errorService) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniProgramEncounterRepository = avniProgramEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.programEncounterService = programEncounterService;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.entityStatusService = entityStatusService;
        this.errorService = errorService;
    }

    public void processProgramEncounters() {
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.ProgramEncounter);
            ProgramEncountersResponse response = avniProgramEncounterRepository.getProgramEncounters(status.getReadUpto());
            ProgramEncounter[] programEncounters = response.getContent();
            int totalElements = response.getTotalElements();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d program encounters that are newer than %s", programEncounters.length, status.getReadUpto()));
            if (programEncounters.length == 0) break;
            for (ProgramEncounter programEncounter : programEncounters) {
                processProgramEncounter(programEncounter);
            }
            if (totalElements == 1 && totalPages == 1) break;
        }
    }

    private void removeIgnoredObservations(ProgramEncounter programEncounter) {
        var observations = (LinkedHashMap<String, Object>) programEncounter.get("observations");
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        programEncounter.set("observations", observations);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processProgramEncounter(ProgramEncounter programEncounter) {
        if (errorService.hasAvniMultipleSubjectsError(programEncounter.getSubjectId())) {
            logger.error(String.format("Skipping Avni encounter %s because of multiple subjects with same id error", programEncounter.getUuid()));
            return;
        }
        removeIgnoredObservations(programEncounter);
        logger.debug(String.format("Processing avni program encounter %s", programEncounter.getUuid()));

        if (programEncounterService.shouldFilterEncounter(programEncounter)) {
            logger.warn(String.format("Program encounter should be filtered out: %s", programEncounter.getUuid()));
            return;
        }

        var subject = avniSubjectRepository.getSubject(programEncounter.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        Pair<OpenMRSPatient, OpenMRSFullEncounter> patientEncounter = programEncounterService.findCommunityEncounter(programEncounter, subject, constants, metaData);
        var patient = patientEncounter.getValue0();
        var encounter = patientEncounter.getValue1();

        if (patient != null && encounter == null) {
            programEncounterService.createCommunityEncounter(programEncounter, patient, constants);
        } else if (patient != null && encounter != null) {
            programEncounterService.updateCommunityEncounter(encounter, programEncounter, constants);
        } else if (patient == null && encounter == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData)));
            programEncounterService.processPatientNotFound(programEncounter);
        }

        entityStatusService.saveEntityStatus(programEncounter);
    }

    @Override
    public void processError(String entityUuid) {
        ProgramEncounter programEncounter = avniProgramEncounterRepository.getProgramEncounter(entityUuid);
        if (programEncounter == null) {
            logger.warn(String.format("ProgramEncounter has been deleted now: %s", entityUuid));
            errorService.errorOccurred(entityUuid, ErrorType.EntityIsDeleted, AvniEntityType.ProgramEncounter);
            return;
        }

        processProgramEncounter(programEncounter);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}