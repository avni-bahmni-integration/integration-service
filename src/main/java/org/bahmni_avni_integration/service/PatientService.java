package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSEncounterRepository;
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
    private AvniEntityStatusRepository avniEntityStatusRepository;
    @Autowired
    private ErrorService errorService;

    public void updateSubject(OpenMRSPatient patient, Subject subject, String encounterTypeUuid, Constants constants) {
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), encounterTypeUuid, constants);
        openMRSEncounterRepository.updateEncounter(encounter);

        saveEntityStatus(subject);

        errorService.successfullyProcessed(subject);
    }

    private void saveEntityStatus(Subject subject) {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
        status.setReadUpto(subject.getLastModifiedDate());
        avniEntityStatusRepository.save(status);
    }

    public void createSubject(Constants constants, String encounterTypeUuid, Subject subject, OpenMRSPatient patient) {
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), encounterTypeUuid, constants);
        openMRSEncounterRepository.createEncounter(encounter);
        saveEntityStatus(subject);

        errorService.successfullyProcessed(subject);
    }

    public Pair<OpenMRSPatient, OpenMRSEncounter> findSubject(Subject subject, Constants constants, String avniIdentifierConcept, String subjectUuidConceptUuid) {
        String subjectId = subject.getUuid();
        String patientIdentifier = constants.getValue(ConstantKey.BahmniIdentifierPrefix) + subject.getObservation(avniIdentifierConcept);
        return openMRSEncounterRepository.getEncounter(patientIdentifier, subjectId, subjectUuidConceptUuid);
    }

    public void processPatientIdChanged(Subject subject) {
        errorService.errorOccurred(subject, ErrorType.PatientIdChanged);
    }

    public void processPatientNotFound(Subject subject) {
        errorService.errorOccurred(subject, ErrorType.NoPatientWithId);
    }
}