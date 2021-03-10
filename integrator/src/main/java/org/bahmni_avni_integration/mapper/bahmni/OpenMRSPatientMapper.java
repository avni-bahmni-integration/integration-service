package org.bahmni_avni_integration.mapper.bahmni;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPersonAttribute;
import org.bahmni_avni_integration.integration_data.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.util.FormatAndParseUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpenMRSPatientMapper {
    public static GeneralEncounter mapToAvniEncounter(OpenMRSPatient openMRSPatient, Subject subject, PatientToSubjectMetaData patientToSubjectMetaData, MappingMetaDataCollection conceptMetaData) {
        LinkedHashMap<String, Object> observations = mapToAvniObservations(openMRSPatient, patientToSubjectMetaData, conceptMetaData);

        GeneralEncounter encounterRequest = new GeneralEncounter();
        encounterRequest.set("Subject ID", subject.getUuid());
        encounterRequest.set("Encounter type", patientToSubjectMetaData.patientEncounterType());
        encounterRequest.set("Encounter date time", FormatAndParseUtil.now());
        encounterRequest.set("observations", observations);
        encounterRequest.set("cancelObservations", new HashMap<>());
        return encounterRequest;
    }

    public static LinkedHashMap<String, Object> mapToAvniObservations(OpenMRSPatient openMRSPatient, PatientToSubjectMetaData patientToSubjectMetaData, MappingMetaDataCollection conceptMetaData) {
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
                MappingMetaData answerMapping = conceptMetaData.getMappingForBahmniValue(attributeUuid);
                observations.put(questionMapping.getAvniValue(), answerMapping.getAvniValue());
            } else {
                observations.put(questionMapping.getAvniValue(), attributeValue);
            }
        }
        observations.put(patientToSubjectMetaData.bahmniEntityUuidConcept(), openMRSPatient.getUuid());
        return observations;
    }
}