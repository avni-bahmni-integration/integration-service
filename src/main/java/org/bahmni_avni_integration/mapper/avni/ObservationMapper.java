package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation.createVoidedObs;

@Component
public class ObservationMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;

    public ObservationMapper(MappingMetaDataRepository mappingMetaDataRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public List<OpenMRSSaveObservation> updateOpenMRSObservationsFromAvniObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations) {
        List<OpenMRSSaveObservation> updateObservations = new ArrayList<>();
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        updateObservations.addAll(voidedObservations(openMRSObservations, avniObservations, conceptMappings));
        return updateObservations;
    }

    private List<OpenMRSSaveObservation> voidedObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations, MappingMetaDataCollection conceptMappings) {
        List<OpenMRSSaveObservation> voidedObservations = new ArrayList<>();
        openMRSObservations.forEach(openMRSObservation -> {
            String conceptUuid = openMRSObservation.getUuid();
            String avniConceptName = conceptMappings.getAvniValueForBahmniValue(conceptUuid);
            Object avniObsValue = avniObservations.get(avniConceptName);

            if (avniObsValue == null) {
                voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid()));
            } else {
                if (avniObsValue instanceof List<?>) {
                    List<String> valueList = (List<String>) avniObsValue;
                    String avniAnswerName = conceptMappings.getAvniValueForBahmniValue((String) openMRSObservation.getValue());
                    if (!valueList.contains(avniAnswerName)) {
                        voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid()));
                    }
                }
            }
        });
        return voidedObservations;
    }

    public void mapObservations(LinkedHashMap<String, Object> avniObservations, OpenMRSEncounter openMRSEncounter) {
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        avniObservations.forEach((key, value) -> {
            MappingMetaData questionMapping = conceptMappings.getMappingForAvniValue(key);
            if (questionMapping != null) {
                if (ObsDataType.Coded.equals(questionMapping.getDataTypeHint())) {
                    if (value instanceof String) {
                        MappingMetaData answerMapping = conceptMappings.getMappingForAvniValue((String) value);
                        openMRSEncounter.addObservation(OpenMRSSaveObservation.createCodedObs(questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                    } else if (value instanceof List<?>) {
                        List<String> valueList = (List<String>) value;
                        valueList.forEach(s -> {
                            MappingMetaData answerMapping = conceptMappings.getMappingForAvniValue(s);
                            openMRSEncounter.addObservation(OpenMRSSaveObservation.createCodedObs(questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                        });
                    }
                } else {
                    openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(questionMapping.getBahmniValue(), value, questionMapping.getDataTypeHint()));
                }
            }
        });
    }

}