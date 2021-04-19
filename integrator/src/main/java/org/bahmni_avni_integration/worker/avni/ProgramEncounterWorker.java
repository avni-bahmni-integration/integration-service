package org.bahmni_avni_integration.worker.avni;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniProgramEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.ProgramEncounterService;
import org.bahmni_avni_integration.worker.ErrorRecordWorker;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

@Component
public class ProgramEncounterWorker implements ErrorRecordWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniProgramEncounterRepository avniProgramEncounterRepository;
    private final MappingMetaDataService mappingMetaDataService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final ProgramEncounterService programEncounterService;

    private static Logger logger = Logger.getLogger(ProgramEncounterWorker.class);
    private EntityStatusService entityStatusService;

    public ProgramEncounterWorker(AvniEntityStatusRepository avniEntityStatusRepository,
                                  MappingMetaDataService mappingMetaDataService,
                                  AvniProgramEncounterRepository avniProgramEncounterRepository,
                                  AvniSubjectRepository avniSubjectRepository, ProgramEncounterService programEncounterService, EntityStatusService entityStatusService) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniProgramEncounterRepository = avniProgramEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.programEncounterService = programEncounterService;
        this.entityStatusService = entityStatusService;
    }

    public void processProgramEncounters(Constants constants, Predicate<ProgramEncounter> continueAfterOneRecord) {
        SubjectToPatientMetaData subjectToPatientMetaData = mappingMetaDataService.getForSubjectToPatient();
        mainLoop:
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.ProgramEncounter);
            ProgramEncounter[] programEncounters = avniProgramEncounterRepository.getProgramEncounters(status.getReadUpto());
            logger.info(String.format("Found %d program encounters that are newer than %s", programEncounters.length, status.getReadUpto()));
            if (programEncounters.length == 0) break;
            for (ProgramEncounter programEncounter : programEncounters) {
                if (processProgramEncounter(constants, continueAfterOneRecord, programEncounter, subjectToPatientMetaData))
                    break mainLoop;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean processProgramEncounter(Constants constants, Predicate<ProgramEncounter> continueAfterOneRecord, ProgramEncounter programEncounter, SubjectToPatientMetaData subjectToPatientMetaData) {
        logger.debug(String.format("Processing avni program encounter %s", programEncounter.getUuid()));

        if (programEncounterService.shouldFilterEncounter(programEncounter)) {
            logger.warn(String.format("Program encounter should be filtered out: %s", programEncounter.getUuid()));
            return !continueAfterOneRecord.test(programEncounter);
        }

        var subject = avniSubjectRepository.getSubject(programEncounter.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        Pair<OpenMRSUuidHolder, OpenMRSFullEncounter> patientEncounter = programEncounterService.findCommunityEncounter(programEncounter, subject, constants, subjectToPatientMetaData);
        var patient = patientEncounter.getValue0();
        var encounter = patientEncounter.getValue1();

        if (patient != null && encounter == null) {
            logger.debug(String.format("Creating new Bahmni Program Encounter for Avni program encounter %s", programEncounter.getUuid()));
            programEncounterService.createCommunityEncounter(programEncounter, patient, constants);
        } else if (patient != null && encounter != null) {
            logger.debug(String.format("Updating existing Bahmni Program Encounter %s", encounter.getUuid()));
            programEncounterService.updateCommunityEncounter(encounter, programEncounter, constants);
        } else if (patient != null && encounter == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(subjectToPatientMetaData)));
            programEncounterService.processPatientNotFound(programEncounter);
        }

        entityStatusService.saveEntityStatus(programEncounter);

        return !continueAfterOneRecord.test(programEncounter);
    }

    public void processProgramEncounters(Constants constants) {
        this.processProgramEncounters(constants, programEncounter -> true);
    }

    @Override
    public void processError(String entityUuid) {
        throw new NotImplementedException();
    }
}