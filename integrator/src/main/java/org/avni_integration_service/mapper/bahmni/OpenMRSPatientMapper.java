package org.avni_integration_service.mapper.bahmni;

import org.avni_integration_service.contract.avni.GeneralEncounter;
import org.avni_integration_service.contract.avni.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.bahmni.contract.OpenMRSPersonAttribute;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.internal.PatientToSubjectMetaData;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.MappingMetaDataCollection;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.AvniFormatAndParseUtil;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OpenMRSPatientMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    public OpenMRSPatientMapper(MappingMetaDataRepository mappingMetaDataRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public GeneralEncounter mapToAvniEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData, MappingMetaDataCollection conceptMetaData) {
        LinkedHashMap<String, Object> observations = mapToAvniObservations(openMRSPatient, patientToSubjectMetaData, conceptMetaData);

        GeneralEncounter encounterRequest = new GeneralEncounter();
        encounterRequest.setSubjectId(subject.getUuid());
        encounterRequest.setEncounterType(patientToSubjectMetaData.patientEncounterType());
        encounterRequest.setEncounterDateTime(FormatAndParseUtil.now());
        encounterRequest.set("observations", observations);
        encounterRequest.set("cancelObservations", new HashMap<>());
        return encounterRequest;
    }

    public LinkedHashMap<String, Object> mapToAvniObservations(OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData, MappingMetaDataCollection conceptMetaData) {
        LinkedHashMap<String, Object> observations = new LinkedHashMap<>();
        for (OpenMRSPersonAttribute openMRSPersonAttribute : openMRSPatient.getPerson().getAttributes()) {
            String attributeTypeUuid = openMRSPersonAttribute.getAttributeType().getUuid();
            MappingMetaData questionMapping = conceptMetaData.getMappingForBahmniValue(attributeTypeUuid);
            Object attributeValue = openMRSPersonAttribute.getValue();
            if (attributeValue == null)
                continue;

            if (attributeValue instanceof Map) {
                Map<String, String> attributeValueMap = (Map<String, String>) attributeValue;
                String attributeUuid = attributeValueMap.get("uuid");
                MappingMetaData answerMapping = mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup.Observation, MappingType.Concept, attributeUuid);
                if (answerMapping == null) {
                    throw new RuntimeException(String.format("Could not find concept mapped for OpenMRS concept: %s while finding answer to OpenMRS concept/person-attribute: %s which is Avni Concept %s", attributeUuid, attributeTypeUuid, questionMapping.getAvniValue()));
                }
                conceptMetaData.getMappingForBahmniValue(attributeUuid);
                observations.put(questionMapping.getAvniValue(), answerMapping.getAvniValue());
            } else {
                observations.put(questionMapping.getAvniValue(), attributeValue);
            }
        }
        observations.put(patientToSubjectMetaData.bahmniEntityUuidConcept(), openMRSPatient.getUuid());
        return observations;
    }
}
