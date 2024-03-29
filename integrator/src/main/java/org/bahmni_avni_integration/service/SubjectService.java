package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.internal.BahmniToAvniMetaData;
import org.bahmni_avni_integration.integration_data.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSPatientMapper;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubjectService {
    private static final Logger logger = Logger.getLogger(SubjectService.class);
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ErrorService errorService;
    private OpenMRSPatientMapper openMRSPatientMapper;

    public SubjectService(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository, MappingMetaDataRepository mappingMetaDataRepository, ErrorService errorService, OpenMRSPatientMapper openMRSPatientMapper) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.errorService = errorService;
        this.openMRSPatientMapper = openMRSPatientMapper;
    }

    public Subject findSubject(OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData, Constants constants) {
        String identifier = openMRSPatient.getPatientId();
        LinkedHashMap<String, Object> subjectCriteria = new LinkedHashMap<>();
        String prefix = constants.getValue(ConstantKey.BahmniIdentifierPrefix);
        subjectCriteria.put(patientToSubjectMetaData.avniIdentifierConcept(), identifier.replace(prefix, ""));
        return avniSubjectRepository.getSubject(
                new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime(),
                constants.getValue(ConstantKey.IntegrationAvniSubjectType),
                subjectCriteria
        );
    }

    public Subject[] findSubjects(Subject subject, SubjectToPatientMetaData subjectToPatientMetaData, Constants constants) {
        LinkedHashMap<String, Object> subjectCriteria = new LinkedHashMap<>();
        subjectCriteria.put(subjectToPatientMetaData.avniIdentifierConcept(), subject.getId(subjectToPatientMetaData));
        return avniSubjectRepository.getSubjects(
                new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime(),
                constants.getValue(ConstantKey.IntegrationAvniSubjectType),
                subjectCriteria
        );
    }

    public void createRegistrationEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData) {
        if (openMRSPatient.isVoided()) return;

        MappingMetaDataCollection conceptMetaData = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept);
        GeneralEncounter encounterRequest = openMRSPatientMapper.mapToAvniEncounter(openMRSPatient, subject, patientToSubjectMetaData, conceptMetaData);
        avniEncounterRepository.create(encounterRequest);

        errorService.successfullyProcessed(openMRSPatient);
    }

    public void updateRegistrationEncounter(GeneralEncounter encounterRequest, OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData) {
        MappingMetaDataCollection conceptMetaData = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept);
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
