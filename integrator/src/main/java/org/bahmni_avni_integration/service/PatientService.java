package org.bahmni_avni_integration.service;

import org.apache.http.HttpStatus;
import org.bahmni_avni_integration.client.bahmni.WebClientsException;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
import org.ict4h.atomfeed.client.domain.Event;
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
    @Autowired
    private OpenMRSPatientRepository patientRepository;

    public void updateSubject(OpenMRSFullEncounter existingEncounter, OpenMRSUuidHolder patient, Subject subject, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToExistingEncounter(existingEncounter, subject, patient.getUuid(), subjectToPatientMetaData.encounterTypeUuid(), constants);
        openMRSEncounterRepository.updateEncounter(encounter);

        errorService.successfullyProcessed(subject);
    }

    public OpenMRSFullEncounter createSubject(Subject subject, OpenMRSUuidHolder patient, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), subjectToPatientMetaData.encounterTypeUuid(), constants);
        OpenMRSFullEncounter savedEncounter = openMRSEncounterRepository.createEncounter(encounter);

        errorService.successfullyProcessed(subject);
        return savedEncounter;
    }

    public Pair<OpenMRSUuidHolder, OpenMRSFullEncounter> findSubject(Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        String subjectId = subject.getUuid();
        OpenMRSUuidHolder patient = findPatient(subject, constants, subjectToPatientMetaData);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        OpenMRSFullEncounter encounter = openMRSEncounterRepository.getEncounterByPatientAndObservation(patient.getUuid(), subjectToPatientMetaData.subjectUuidConceptUuid(), subjectId);
        return new Pair<>(patient, encounter);
    }

    public OpenMRSUuidHolder findPatient(Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        String patientIdentifier = constants.getValue(ConstantKey.BahmniIdentifierPrefix) + subject.getId(subjectToPatientMetaData);
        OpenMRSUuidHolder patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        return patient;
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

    public OpenMRSPatient getPatient(Event event) {
        try {
            return patientRepository.getPatient(event);
        } catch (WebClientsException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public boolean shouldFilterPatient(OpenMRSPatient patient, Constants constants) {
        String patientId = patient.getPatientId();
        return !patientId.startsWith(constants.getValue(ConstantKey.BahmniIdentifierPrefix));
    }
}