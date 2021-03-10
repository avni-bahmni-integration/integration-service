package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.internal.BahmniToAvniMetaData;
import org.bahmni_avni_integration.integration_data.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSPatientMapper;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubjectService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ErrorService errorService;

    public SubjectService(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository, MappingMetaDataRepository mappingMetaDataRepository, ErrorService errorService) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.errorService = errorService;
    }

    public Subject findSubject(OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData, Constants constants) {
        String identifier = openMRSPatient.getPatientId();
        LinkedHashMap<String, Object> subjectCriteria = new LinkedHashMap<>();
        String prefix = constants.getValue(ConstantKey.BahmniIdentifierPrefix);
        subjectCriteria.put(patientToSubjectMetaData.avniIdentifierConcept(), identifier.replace(prefix, ""));
        return avniSubjectRepository.getSubject(
                new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime(),
                patientToSubjectMetaData.subjectType(),
                subjectCriteria
        );
    }

    public GeneralEncounter createRegistrationEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData) {
        MappingMetaDataCollection conceptMetaData = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept);
        GeneralEncounter encounterRequest = OpenMRSPatientMapper.mapToAvniEncounter(openMRSPatient, subject, patientToSubjectMetaData, conceptMetaData);
        return avniEncounterRepository.create(encounterRequest);
    }

    public GeneralEncounter updateRegistrationEncounter(GeneralEncounter encounterRequest, OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData) {
        MappingMetaDataCollection conceptMetaData = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept);
        encounterRequest.set("observations", OpenMRSPatientMapper.mapToAvniObservations(openMRSPatient, patientToSubjectMetaData, conceptMetaData));
        return avniEncounterRepository.update((String) encounterRequest.get("ID"), encounterRequest);
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