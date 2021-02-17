package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSBaseEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPostSaveEncounter;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private OpenMRSEncounterRepository openMRSEncounterRepository;
    @Autowired
    private OpenMRSPatientRepository openMRSPatientRepository;
    @Autowired
    private ErrorService errorService;

    public void updateSubject(OpenMRSPatient patient, Subject subject, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), subjectToPatientMetaData.encounterTypeUuid(), constants);
        openMRSEncounterRepository.updateEncounter(encounter);

        errorService.successfullyProcessed(subject);
    }

    public OpenMRSPostSaveEncounter createSubject(Subject subject, OpenMRSPatient patient, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), subjectToPatientMetaData.encounterTypeUuid(), constants);
        OpenMRSPostSaveEncounter savedEncounter = openMRSEncounterRepository.createEncounter(encounter);

        errorService.successfullyProcessed(subject);
        return savedEncounter;
    }

    public Pair<OpenMRSPatient, OpenMRSEncounter> findSubject(Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        String subjectId = subject.getUuid();
        String patientIdentifier = constants.getValue(ConstantKey.BahmniIdentifierPrefix) + subject.getId(subjectToPatientMetaData);
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        OpenMRSEncounter encounter = openMRSEncounterRepository.getRegistrationEncounterForAvniSubject(patient, subjectId, subjectToPatientMetaData.subjectUuidConceptUuid());
        return new Pair<>(patient, encounter);
    }

    public void processPatientIdChanged(Subject subject, SubjectToPatientMetaData metaData) {
        errorService.errorOccurred(subject, ErrorType.PatientIdChanged, metaData);
    }

    public void processPatientNotFound(Subject subject, SubjectToPatientMetaData metaData) {
        errorService.errorOccurred(subject, ErrorType.NoPatientWithId, metaData);
    }

//    doesn't work
    public void deleteSubject(OpenMRSBaseEncounter encounter) {
        openMRSEncounterRepository.deleteEncounter(encounter);
    }
}