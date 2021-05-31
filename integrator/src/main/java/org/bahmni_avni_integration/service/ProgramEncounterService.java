package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.mapper.avni.ProgramEncounterMapper;
import org.bahmni_avni_integration.worker.avni.ProgramEncounterWorker;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

@Service
public class ProgramEncounterService {

    private final PatientService patientService;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final OpenMRSEncounterRepository openMRSEncounterRepository;
    private final VisitService visitService;
    private final ProgramEncounterMapper programEncounterMapper;
    private final ErrorService errorService;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private static final Logger logger = Logger.getLogger(ProgramEncounterService.class);

    public ProgramEncounterService(PatientService patientService,
                                   MappingMetaDataRepository mappingMetaDataRepository,
                                   OpenMRSEncounterRepository openMRSEncounterRepository,
                                   VisitService visitService,
                                   ProgramEncounterMapper programEncounterMapper,
                                   ErrorService errorService,
                                   AvniEnrolmentRepository avniEnrolmentRepository) {
        this.patientService = patientService;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.visitService = visitService;
        this.programEncounterMapper = programEncounterMapper;
        this.errorService = errorService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
    }

    public Pair<OpenMRSPatient, OpenMRSFullEncounter> findCommunityEncounter(ProgramEncounter programEncounter, Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        OpenMRSPatient patient = patientService.findPatient(subject, constants, subjectToPatientMetaData);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        String entityUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        OpenMRSFullEncounter encounter = openMRSEncounterRepository
                .getEncounterByPatientAndObservation(patient.getUuid(), entityUuidConcept, programEncounter.getUuid());
        return new Pair<>(patient, encounter);
    }

    public OpenMRSFullEncounter createCommunityEncounter(ProgramEncounter programEncounter, OpenMRSPatient patient, Constants constants) {
        if (programEncounter.getVoided()) {
            logger.debug(String.format("Skipping voided Avni encounter %s", programEncounter.getUuid()));
            return null;
        }

        logger.debug(String.format("Creating new Bahmni Encounter for Avni encounter %s", programEncounter.getUuid()));
        var enrolment = avniEnrolmentRepository.getEnrolment(programEncounter.getEnrolmentId());
        var visit = visitService.getOrCreateVisit(patient, enrolment);
        var encounter = programEncounterMapper.mapEncounter(programEncounter, patient.getUuid(), constants, visit);
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
            var openMRSEncounter = programEncounterMapper.mapProgramEncounterToExistingEncounter(existingEncounter,
                    programEncounter,
                    constants);
            openMRSEncounterRepository.updateEncounter(openMRSEncounter);
            errorService.successfullyProcessed(programEncounter);
        }
    }
}