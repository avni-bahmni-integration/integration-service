package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.avni.domain.AvniBaseEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.bahmni.SubjectToPatientMetaData;
import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
import org.javatuples.Pair;

public abstract class BaseAvniEncounterService {
    protected PatientService patientService;
    protected MappingService mappingService;
    protected OpenMRSEncounterRepository openMRSEncounterRepository;

    public BaseAvniEncounterService(PatientService patientService, MappingService mappingService, OpenMRSEncounterRepository openMRSEncounterRepository) {
        this.patientService = patientService;
        this.mappingService = mappingService;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
    }

    public Pair<OpenMRSPatient, OpenMRSFullEncounter> findCommunityEncounter(AvniBaseEncounter avniBaseEncounter, Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        OpenMRSPatient patient = patientService.findPatient(subject, constants, subjectToPatientMetaData);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        String entityUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        OpenMRSFullEncounter encounter = openMRSEncounterRepository
                .getEncounterByPatientAndObservation(patient.getUuid(), entityUuidConcept, avniBaseEncounter.getUuid());
        return new Pair<>(patient, encounter);
    }
}
