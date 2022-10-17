package org.avni_integration_service.bahmni.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.ProgramEncounter;
import org.avni_integration_service.avni.domain.ProgramEncountersResponse;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniProgramEncounterRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.bahmni.BahmniErrorType;
import org.avni_integration_service.bahmni.SubjectToPatientMetaData;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.bahmni.service.AvniBahmniErrorService;
import org.avni_integration_service.bahmni.service.AvniEntityStatusService;
import org.avni_integration_service.bahmni.service.MappingMetaDataService;
import org.avni_integration_service.bahmni.service.ProgramEncounterService;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class ProgramEncounterWorker implements ErrorRecordWorker {
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final AvniProgramEncounterRepository avniProgramEncounterRepository;
    private final MappingMetaDataService mappingMetaDataService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final ProgramEncounterService programEncounterService;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;

    private static final Logger logger = Logger.getLogger(ProgramEncounterWorker.class);
    private final AvniEntityStatusService avniEntityStatusService;
    private Constants constants;
    private SubjectToPatientMetaData metaData;
    private final AvniBahmniErrorService avniBahmniErrorService;

    public ProgramEncounterWorker(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                  MappingMetaDataService mappingMetaDataService,
                                  AvniProgramEncounterRepository avniProgramEncounterRepository,
                                  AvniSubjectRepository avniSubjectRepository,
                                  ProgramEncounterService programEncounterService,
                                  AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                  AvniEntityStatusService avniEntityStatusService,
                                  AvniBahmniErrorService avniBahmniErrorService) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniProgramEncounterRepository = avniProgramEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.programEncounterService = programEncounterService;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniEntityStatusService = avniEntityStatusService;
        this.avniBahmniErrorService = avniBahmniErrorService;
    }

    public void processProgramEncounters() {
        while (true) {
            IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(AvniEntityType.ProgramEncounter.name());
            ProgramEncountersResponse response = avniProgramEncounterRepository.getProgramEncounters(status.getReadUptoDateTime());
            ProgramEncounter[] programEncounters = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d program encounters that are newer than %s", programEncounters.length, status.getReadUptoDateTime()));
            if (programEncounters.length == 0) break;
            for (ProgramEncounter programEncounter : programEncounters) {
                processProgramEncounter(programEncounter, true);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            };
        }
    }

    private void removeIgnoredObservations(ProgramEncounter programEncounter) {
        var observations = (Map<String, Object>) programEncounter.getObservations();
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        programEncounter.setObservations(observations);
    }

    private void updateSyncStatus(ProgramEncounter programEncounter, boolean updateSyncStatus) {
        if (updateSyncStatus)
            avniEntityStatusService.saveEntityStatus(programEncounter);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processProgramEncounter(ProgramEncounter programEncounter, boolean updateSyncStatus) {
        if (mappingMetaDataService.isBahmniEncounterInAvni(programEncounter.getEncounterType())) {
            logger.debug(String.format("Skipping Avni program encounter %s because it was created from Bahmni", programEncounter.getEncounterType()));
            updateSyncStatus(programEncounter, updateSyncStatus);
            return;
        }

        if (avniBahmniErrorService.hasAvniMultipleSubjectsError(programEncounter.getSubjectId())) {
            logger.error(String.format("Skipping Avni encounter %s because of multiple subjects with same id error", programEncounter.getUuid()));
            avniBahmniErrorService.errorOccurred(programEncounter, BahmniErrorType.MultipleSubjectsWithId);
            updateSyncStatus(programEncounter, updateSyncStatus);
            return;
        }
        removeIgnoredObservations(programEncounter);
        logger.debug(String.format("Processing avni program encounter %s", programEncounter.getUuid()));

        if (programEncounterService.shouldFilterEncounter(programEncounter)) {
            logger.warn(String.format("Program encounter should be filtered out: %s", programEncounter.getUuid()));
            updateSyncStatus(programEncounter, updateSyncStatus);
            return;
        }

        var subject = avniSubjectRepository.getSubject(programEncounter.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        if (subject.getVoided()) {
            logger.debug(String.format("Avni subject is voided. Skipping. %s", subject.getUuid()));
            updateSyncStatus(programEncounter, updateSyncStatus);
            return;
        }
        Pair<OpenMRSPatient, OpenMRSFullEncounter> patientEncounter = programEncounterService.findCommunityEncounter(programEncounter, subject, constants, metaData);
        var patient = patientEncounter.getValue0();
        var encounter = patientEncounter.getValue1();

        if (patient != null && encounter == null) {
            programEncounterService.createCommunityEncounter(programEncounter, patient, constants);
        } else if (patient != null && encounter != null) {
            programEncounterService.updateCommunityEncounter(encounter, programEncounter, constants);
        } else if (patient == null && encounter == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData.avniIdentifierConcept())));
            programEncounterService.processPatientNotFound(programEncounter);
        }

        updateSyncStatus(programEncounter, updateSyncStatus);
    }

    @Override
    public void processError(String entityUuid) {
        ProgramEncounter programEncounter = avniProgramEncounterRepository.getProgramEncounter(entityUuid);
        if (programEncounter == null) {
            logger.warn(String.format("ProgramEncounter has been deleted now: %s", entityUuid));
            avniBahmniErrorService.errorOccurred(entityUuid, BahmniErrorType.EntityIsDeleted, AvniEntityType.ProgramEncounter);
            return;
        }

        processProgramEncounter(programEncounter, false);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}
