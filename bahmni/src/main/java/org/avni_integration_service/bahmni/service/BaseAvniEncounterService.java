package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.contract.avni.AvniBaseEncounter;
import org.avni_integration_service.contract.avni.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.internal.SubjectToPatientMetaData;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
import org.javatuples.Pair;

public abstract class BaseAvniEncounterService {
    protected PatientService patientService;
    protected MappingMetaDataRepository mappingMetaDataRepository;
    protected OpenMRSEncounterRepository openMRSEncounterRepository;

    public BaseAvniEncounterService(PatientService patientService, MappingMetaDataRepository mappingMetaDataRepository, OpenMRSEncounterRepository openMRSEncounterRepository) {
        this.patientService = patientService;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
    }

    public Pair<OpenMRSPatient, OpenMRSFullEncounter> findCommunityEncounter(AvniBaseEncounter avniBaseEncounter, Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        OpenMRSPatient patient = patientService.findPatient(subject, constants, subjectToPatientMetaData);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        String entityUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        OpenMRSFullEncounter encounter = openMRSEncounterRepository
                .getEncounterByPatientAndObservation(patient.getUuid(), entityUuidConcept, avniBaseEncounter.getUuid());
        return new Pair<>(patient, encounter);
    }
}
