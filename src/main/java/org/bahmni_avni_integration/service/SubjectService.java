package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPersonAttribute;
import org.bahmni_avni_integration.contract.internal.BahmniToAvniMetaData;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
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

    public Encounter createRegistrationEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData) {
        Map<String, Object> observations = mapObservations(openMRSPatient);
        observations.put(patientToSubjectMetaData.bahmniEntityUuidConcept(), openMRSPatient.getUuid());

        Encounter encounterRequest = new Encounter();
        encounterRequest.set("Subject ID", subject.getUuid());
        encounterRequest.set("Encounter type", patientToSubjectMetaData.patientEncounterType());
        encounterRequest.set("Encounter date time", FormatAndParseUtil.now());
        encounterRequest.set("observations", observations);
        encounterRequest.set("cancelObservations", new HashMap<>());
        return avniEncounterRepository.create(encounterRequest);
    }

    private Map<String, Object> mapObservations(OpenMRSPatient openMRSPatient) {
        LinkedHashMap<String, Object> observations = new LinkedHashMap<>();
        MappingMetaDataCollection conceptMetaData = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept);
        for (OpenMRSPersonAttribute openMRSPersonAttribute : openMRSPatient.getPerson().getAttributes()) {
            String attributeTypeUuid = openMRSPersonAttribute.getAttributeType().getUuid();
            MappingMetaData questionMapping = conceptMetaData.getMappingForBahmniValue(attributeTypeUuid);
            Object attributeValue = openMRSPersonAttribute.getValue();
            if (attributeValue instanceof Map) {
                Map<String, String> attributeValueMap = (Map<String, String>) attributeValue;
                String attributeUuid = attributeValueMap.get("uuid");
                MappingMetaData answerMapping = conceptMetaData.getMappingForBahmniValue(attributeUuid);
                observations.put(questionMapping.getAvniValue(), answerMapping.getAvniValue());
            } else {
                observations.put(questionMapping.getAvniValue(), attributeValue);
            }
        }
        logger.debug(String.format("Obs %s", observations.toString()));
        return observations;
    }

    public Encounter updateRegistrationEncounter(Encounter encounterRequest, OpenMRSPatient openMRSPatient) {
        encounterRequest.set("observations", mapObservations(openMRSPatient));
        return avniEncounterRepository.update((String) encounterRequest.get("ID"), encounterRequest);
    }

    public Encounter findPatient(BahmniToAvniMetaData metaData, String externalId) {
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put(metaData.getBahmniEntityUuidConcept(), externalId);
        return avniEncounterRepository.getEncounter(encounterCriteria);
    }

    public void processSubjectIdChanged(OpenMRSPatient patient, PatientToSubjectMetaData metaData) {
        errorService.errorOccurred(patient, ErrorType.SubjectIdChanged, metaData);
    }

    public void processSubjectNotFound(OpenMRSPatient patient, PatientToSubjectMetaData metaData) {
        errorService.errorOccurred(patient, ErrorType.NoSubjectWithId, metaData);
    }

    public void processMultipleSubjectsFound(OpenMRSPatient patient, PatientToSubjectMetaData metaData) {
        errorService.errorOccurred(patient, ErrorType.MultipleSubjectsWithId, metaData);
    }
}