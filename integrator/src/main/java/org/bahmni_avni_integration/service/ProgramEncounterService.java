package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.mapper.avni.EncounterMapper;
import org.springframework.stereotype.Service;

@Service
public class ProgramEncounterService extends BaseAvniEncounterService {
    private final VisitService visitService;
    private final EncounterMapper encounterMapper;
    private final ErrorService errorService;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private static final Logger logger = Logger.getLogger(ProgramEncounterService.class);

    public ProgramEncounterService(PatientService patientService,
                                   MappingMetaDataRepository mappingMetaDataRepository,
                                   OpenMRSEncounterRepository openMRSEncounterRepository,
                                   VisitService visitService,
                                   EncounterMapper encounterMapper,
                                   ErrorService errorService,
                                   AvniEnrolmentRepository avniEnrolmentRepository) {
        super(patientService, mappingMetaDataRepository, openMRSEncounterRepository);
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
        errorService.errorOccurred(programEncounter, ErrorType.NoPatientWithId);
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
