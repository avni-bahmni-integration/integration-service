package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.AvniBaseEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
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
