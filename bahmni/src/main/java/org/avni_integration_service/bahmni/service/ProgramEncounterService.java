package org.avni_integration_service.bahmni.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniErrorType;
import org.avni_integration_service.bahmni.mapper.avni.EncounterMapper;
import org.avni_integration_service.avni.domain.ProgramEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.avni.repository.AvniEnrolmentRepository;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.Constants;

import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgramEncounterService extends BaseAvniEncounterService {
    private final VisitService visitService;
    private final EncounterMapper encounterMapper;
    private final ErrorService errorService;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private static final Logger logger = Logger.getLogger(ProgramEncounterService.class);

    public ProgramEncounterService(PatientService patientService,
                                   MappingService mappingService,
                                   OpenMRSEncounterRepository openMRSEncounterRepository,
                                   VisitService visitService,
                                   EncounterMapper encounterMapper,
                                   ErrorService errorService,
                                   AvniEnrolmentRepository avniEnrolmentRepository) {
        super(patientService, mappingService, openMRSEncounterRepository);
        this.visitService = visitService;
        this.encounterMapper = encounterMapper;
        this.errorService = errorService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
    }

    public OpenMRSFullEncounter createCommunityEncounter(ProgramEncounter programEncounter, OpenMRSPatient patient, Constants constants) {
        if (programEncounter.getVoided()) {
            logger.debug(String.format("Skipping voided Avni encounter %s", programEncounter.getUuid()));
            return null;
        }

        logger.debug(String.format("Creating new Bahmni Encounter for Avni encounter %s", programEncounter.getUuid()));
        var enrolment = avniEnrolmentRepository.getEnrolment(programEncounter.getEnrolmentId());
        var visit = visitService.getOrCreateVisit(patient, enrolment);
        var encounter = encounterMapper.mapEncounter(programEncounter, patient.getUuid(), constants, visit);
        var savedEncounter = openMRSEncounterRepository.createEncounter(encounter);

        errorService.successfullyProcessed(programEncounter);
        return savedEncounter;
    }

    public boolean shouldFilterEncounter(ProgramEncounter programEncounter) {
        return !programEncounter.isCompleted();
    }

    public void processPatientNotFound(ProgramEncounter programEncounter) {
        errorService.errorOccurred(programEncounter, BahmniErrorType.NoPatientWithId);
    }

    public void updateCommunityEncounter(OpenMRSFullEncounter existingEncounter, ProgramEncounter programEncounter, Constants constants) {
        if (programEncounter.getVoided()) {
            logger.debug(String.format("Voiding Bahmni Encounter %s because the Avni encounter %s is voided",
                    existingEncounter.getUuid(),
                    programEncounter.getUuid()));
            openMRSEncounterRepository.voidEncounter(existingEncounter);
        } else {
            logger.debug(String.format("Updating existing Bahmni Encounter %s", existingEncounter.getUuid()));
            var openMRSEncounter = encounterMapper.mapEncounterToExistingEncounter(existingEncounter,
                    programEncounter,
                    constants);
            openMRSEncounterRepository.updateEncounter(openMRSEncounter);
            errorService.successfullyProcessed(programEncounter);
        }
    }
}
