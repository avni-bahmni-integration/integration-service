package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPersonAttribute;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaData;
import org.bahmni_avni_integration.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SubjectService {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AvniEncounterRepository avniEncounterRepository;

    private final AvniSubjectRepository avniSubjectRepository;

    private final MappingMetaDataRepository mappingMetaDataRepository;

    public SubjectService(AvniEncounterRepository avniEncounterRepository, AvniSubjectRepository avniSubjectRepository, MappingMetaDataRepository mappingMetaDataRepository) {
        this.avniEncounterRepository = avniEncounterRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public Subject findSubject(OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData) {
        String identifier = openMRSPatient.getIdentifiers().stream().findFirst().get().getIdentifier();
        LinkedHashMap<String, Object> subjectCriteria = new LinkedHashMap<>();
        subjectCriteria.put(patientToSubjectMetaData.avniIdentifierConcept(), identifier.substring(3));
        return avniSubjectRepository.getSubject(
                new GregorianCalendar(1900, 0, 1).getTime(),
                patientToSubjectMetaData.subjectType(),
                subjectCriteria
        );
    }

    public Encounter createRegistrationEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData) {
        Map<String, Object> observations = mapObservations(openMRSPatient);
        observations.put(patientToSubjectMetaData.patientUuidConcept(), openMRSPatient.getUuid());

        Encounter encounterRequest = new Encounter();
        encounterRequest.set("Subject ID", subject.getUuid());
        encounterRequest.set("Encounter type", patientToSubjectMetaData.patientEncounterType());
        encounterRequest.set("Encounter date time", FormatAndParseUtil.now());
        encounterRequest.set("observations", observations);
        encounterRequest.set("cancelObservations", new HashMap<>());
        Encounter encounter = avniEncounterRepository.create(encounterRequest);
        return encounter;
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
}