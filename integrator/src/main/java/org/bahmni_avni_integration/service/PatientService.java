package org.bahmni_avni_integration.service;

import org.apache.http.HttpStatus;
import org.bahmni_avni_integration.client.bahmni.WebClientsException;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSPatientRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPersonRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final SubjectMapper subjectMapper;
    private final OpenMRSEncounterRepository openMRSEncounterRepository;
    private final OpenMRSPatientRepository openMRSPatientRepository;
    private final ErrorService errorService;
    private final OpenMRSPersonRepository openMRSPersonRepository;
    private final VisitService visitService;

    public PatientService(SubjectMapper subjectMapper, OpenMRSEncounterRepository openMRSEncounterRepository, OpenMRSPatientRepository openMRSPatientRepository, ErrorService errorService, OpenMRSPersonRepository openMRSPersonRepository, VisitService visitService) {
        this.subjectMapper = subjectMapper;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.openMRSPatientRepository = openMRSPatientRepository;
        this.errorService = errorService;
        this.openMRSPersonRepository = openMRSPersonRepository;
        this.visitService = visitService;
    }

    public void updateSubject(OpenMRSFullEncounter existingEncounter, OpenMRSPatient patient, Subject subject, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        if (subject.getVoided()) {
            openMRSEncounterRepository.voidEncounter(existingEncounter);
        } else {
            OpenMRSEncounter encounter = subjectMapper.mapSubjectToExistingEncounter(existingEncounter, subject, patient.getUuid(), subjectToPatientMetaData.encounterTypeUuid(), constants);
            openMRSEncounterRepository.updateEncounter(encounter);
            errorService.successfullyProcessed(subject);
        }
    }

    public OpenMRSFullEncounter createSubject(Subject subject, OpenMRSPatient patient, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        if (subject.getVoided())
            return null;

        var visit = visitService.getOrCreateVisit(patient, subject);
        OpenMRSEncounter encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), subjectToPatientMetaData.encounterTypeUuid(), constants, visit);
        OpenMRSFullEncounter savedEncounter = openMRSEncounterRepository.createEncounter(encounter);

        errorService.successfullyProcessed(subject);
        return savedEncounter;
    }

    public OpenMRSFullEncounter createPatientAndSubject(Subject subject, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        if (subject.getVoided())
            return null;

        var newPatient = createPatient(subject, subjectToPatientMetaData, constants);
        var fullPatientObject = getPatient(newPatient.getUuid());
        return createSubject(subject, fullPatientObject, subjectToPatientMetaData, constants);
    }

    public Pair<OpenMRSPatient, OpenMRSFullEncounter> findSubject(Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        String subjectId = subject.getUuid();
        OpenMRSPatient patient = findPatient(subject, constants, subjectToPatientMetaData);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        OpenMRSFullEncounter encounter = openMRSEncounterRepository.getEncounterByPatientAndObservation(patient.getUuid(), subjectToPatientMetaData.subjectUuidConceptUuid(), subjectId);
        return new Pair<>(patient, encounter);
    }

    public OpenMRSPatient findPatient(Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        String patientIdentifier = constants.getValue(ConstantKey.BahmniIdentifierPrefix) + subject.getId(subjectToPatientMetaData);
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        return patient;
    }

    public void processPatientIdChanged(Subject subject, SubjectToPatientMetaData metaData) {
        errorService.errorOccurred(subject, ErrorType.PatientIdChanged);
    }

    public void processPatientNotFound(Subject subject, SubjectToPatientMetaData metaData) {
        errorService.errorOccurred(subject, ErrorType.NoPatientWithId);
    }

    private OpenMRSPatient createPatient(Subject subject, SubjectToPatientMetaData metaData, Constants constants) {
        OpenMRSSavePerson person = new OpenMRSSavePerson();
        person.setNames(List.of(new OpenMRSSaveName(
                subject.getFirstName(),
                subject.getLastName(),
                true
        )));
        person.setBirthDate(subject.getDateOfBirth());
        person.setGender(FormatAndParseUtil.fromAvniToOpenMRSGender((String) subject.getObservation("Gender")));
        OpenMRSUuidHolder uuidHolder = openMRSPersonRepository.createPerson(person);
        OpenMRSSavePatient patient = new OpenMRSSavePatient();
        patient.setPerson(uuidHolder.getUuid());
        patient.setIdentifiers(List.of(new OpenMRSSavePatientIdentifier(
                String.format("%s%s", constants.getValue(ConstantKey.BahmniIdentifierPrefix), subject.getId(metaData)),
                constants.getValue(ConstantKey.IntegrationBahmniIdentifierType),
                constants.getValue(ConstantKey.IntegrationBahmniLocation),
                true
        )));
        return openMRSPatientRepository.createPatient(patient);
    }

    //    doesn't work
    public void deleteSubject(OpenMRSBaseEncounter encounter) {
        openMRSEncounterRepository.deleteEncounter(encounter);
    }

    public OpenMRSPatient getPatient(Event event) {
        try {
            return openMRSPatientRepository.getPatient(event);
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

    public OpenMRSPatient getPatient(String patientUuid) {
        try {
            return openMRSPatientRepository.getPatient(patientUuid);
        } catch (WebClientsException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public void patientDeleted(String patientUuid) {
        errorService.errorOccurred(patientUuid, ErrorType.EntityIsDeleted, BahmniEntityType.Patient);
    }

    public void notACommunityMember(OpenMRSPatient patient) {
        errorService.errorOccurred(patient, ErrorType.NotACommunityMember);
    }

    public void processMultipleSubjectsFound(Subject subject) {
        errorService.errorOccurred(subject, ErrorType.MultipleSubjectsWithId);
    }

    public void processSubjectIdNull(Subject subject) {
        errorService.errorOccurred(subject, ErrorType.SubjectIdNull);
    }
}