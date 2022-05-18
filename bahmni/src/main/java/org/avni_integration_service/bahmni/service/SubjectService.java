package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.*;
import org.avni_integration_service.bahmni.mapper.OpenMRSPatientMapper;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubjectService {
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingService mappingService;
    private final ErrorService errorService;
    private final OpenMRSPatientMapper openMRSPatientMapper;

    public SubjectService(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository, MappingService mappingService, ErrorService errorService, OpenMRSPatientMapper openMRSPatientMapper) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.mappingService = mappingService;
        this.errorService = errorService;
        this.openMRSPatientMapper = openMRSPatientMapper;
    }

    public Subject findSubject(OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData, Constants constants) {
        String identifier = openMRSPatient.getPatientId();
        LinkedHashMap<String, Object> subjectCriteria = new LinkedHashMap<>();
        String prefix = constants.getValue(ConstantKey.BahmniIdentifierPrefix.name());
        subjectCriteria.put(patientToSubjectMetaData.avniIdentifierConcept(), identifier.replace(prefix, ""));
        return avniSubjectRepository.getSubject(
                new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime(),
                constants.getValue(ConstantKey.IntegrationAvniSubjectType.name()),
                subjectCriteria
        );
    }

    public Subject[] findSubjects(Subject subject, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        LinkedHashMap<String, Object> subjectCriteria = new LinkedHashMap<>();
        subjectCriteria.put(subjectToPatientMetaData.avniIdentifierConcept(), subject.getId(subjectToPatientMetaData.avniIdentifierConcept()));
        return avniSubjectRepository.getSubjects(
                new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime(),
                constants.getValue(ConstantKey.IntegrationAvniSubjectType.name()),
                subjectCriteria
        );
    }

    public void createRegistrationEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData) {
        if (openMRSPatient.isVoided()) return;

        MappingMetaDataCollection conceptMetaData = mappingService.findAll(BahmniMappingGroup.PatientSubject, BahmniMappingType.PersonAttributeConcept);
        GeneralEncounter encounterRequest = openMRSPatientMapper.mapToAvniEncounter(openMRSPatient, subject, patientToSubjectMetaData, conceptMetaData);
        avniEncounterRepository.create(encounterRequest);

        errorService.successfullyProcessed(openMRSPatient);
    }

    public void updateRegistrationEncounter(GeneralEncounter encounterRequest, OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData) {
        MappingMetaDataCollection conceptMetaData = mappingService.findAll(BahmniMappingGroup.PatientSubject, BahmniMappingType.PersonAttributeConcept);
        encounterRequest.set("observations", openMRSPatientMapper.mapToAvniObservations(openMRSPatient, patientToSubjectMetaData, conceptMetaData));
        encounterRequest.setVoided(openMRSPatient.isVoided());
        avniEncounterRepository.update((String) encounterRequest.get("ID"), encounterRequest);

        errorService.successfullyProcessed(openMRSPatient);
    }

    // Patient from OpenMRS/Bahmni is saved as Encounter in Avni
    public GeneralEncounter findPatient(BahmniToAvniMetaData metaData, String externalId) {
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put(metaData.getBahmniEntityUuidConcept(), externalId);
        return avniEncounterRepository.getEncounter(encounterCriteria);
    }

    public void processSubjectIdChanged(OpenMRSPatient patient) {
        errorService.errorOccurred(patient, ErrorType.SubjectIdChanged);
    }

    public void processSubjectNotFound(OpenMRSPatient patient) {
        errorService.errorOccurred(patient, ErrorType.NoSubjectWithId);
    }

    public void processMultipleSubjectsFound(OpenMRSPatient patient) {
        errorService.errorOccurred(patient, ErrorType.MultipleSubjectsWithId);
    }
}
