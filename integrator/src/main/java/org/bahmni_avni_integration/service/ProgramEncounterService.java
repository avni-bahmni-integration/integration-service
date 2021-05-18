package org.bahmni_avni_integration.service;

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
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.mapper.avni.ProgramEncounterMapper;
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

    public ProgramEncounterService(PatientService patientService, MappingMetaDataRepository mappingMetaDataRepository, OpenMRSEncounterRepository openMRSEncounterRepository, VisitService visitService, ProgramEncounterMapper programEncounterMapper, ErrorService errorService) {
        this.patientService = patientService;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.visitService = visitService;
        this.programEncounterMapper = programEncounterMapper;
        this.errorService = errorService;
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
        if (programEncounter.getVoided()) return null;

        var visit = visitService.getOrCreateVisit(patient);
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
            openMRSEncounterRepository.voidEncounter(existingEncounter);
        } else {
            OpenMRSEncounter openMRSEncounter = programEncounterMapper.mapProgramEncounterToExistingEncounter(existingEncounter, programEncounter, constants);
            openMRSEncounterRepository.updateEncounter(openMRSEncounter);
            errorService.successfullyProcessed(programEncounter);
        }
    }
}