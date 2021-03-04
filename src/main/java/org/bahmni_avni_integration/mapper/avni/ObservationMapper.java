package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
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
        updateObservations.addAll(updatedObservations(openMRSObservations, avniObservations, conceptMappings));
        return updateObservations;
    }

    private List<OpenMRSSaveObservation> updatedObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations, MappingMetaDataCollection conceptMappings) {
        List<OpenMRSSaveObservation> updatedObservations = new ArrayList<>();
        avniObservations.forEach((question, answer) -> {
            MappingMetaData questionMapping = conceptMappings.getMappingForAvniValue(question);
            if (questionMapping != null) {
                if (ObsDataType.Coded.equals(questionMapping.getDataTypeHint())) {
                    if (answer instanceof String) {
                        MappingMetaData answerMapping = conceptMappings.getMappingForAvniValue((String) answer);
                        OpenMRSObservation openMRSObservation = openMRSObservations.stream()
                                .filter(o -> o.getConceptUuid().equals(conceptMappings.getBahmniValueForAvniValue(question)) &&
                                        o.getValue().equals(conceptMappings.getBahmniValueForAvniValue((String) answer)))
                                .findFirst()
                                .orElse(null);
                        if (openMRSObservation != null) {
                            updatedObservations.add(OpenMRSSaveObservation.createCodedObs(openMRSObservation.getObsUuid(), questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                        } else {
                            updatedObservations.add(OpenMRSSaveObservation.createCodedObs(questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                        }
                    } else if (answer instanceof List<?>) {
                        List<String> valueList = (List<String>) answer;
                        valueList.forEach(s -> {
                            MappingMetaData answerMapping = conceptMappings.getMappingForAvniValue(s);
                            OpenMRSObservation openMRSObservation = openMRSObservations.stream()
                                    .filter(o -> o.getConceptUuid().equals(conceptMappings.getBahmniValueForAvniValue(question)) &&
                                            o.getValue().equals(conceptMappings.getBahmniValueForAvniValue(s)))
                                    .findFirst()
                                    .orElse(null);
                            if (openMRSObservation != null) {
                                updatedObservations.add(OpenMRSSaveObservation.createCodedObs(openMRSObservation.getObsUuid(), questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                            } else {
                                updatedObservations.add(OpenMRSSaveObservation.createCodedObs(questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                            }
                        });
                    }
                } else {
                    OpenMRSObservation openMRSObservation = openMRSObservations.stream()
                            .filter(o -> o.getConceptUuid().equals(conceptMappings.getBahmniValueForAvniValue(question)))
                            .findFirst()
                            .orElse(null);
                    if (openMRSObservation != null) {
                        updatedObservations.add(OpenMRSSaveObservation.createPrimitiveObs(openMRSObservation.getObsUuid(), openMRSObservation.getConceptUuid(), answer, questionMapping.getDataTypeHint()));
                    } else {
                        updatedObservations.add(OpenMRSSaveObservation.createPrimitiveObs(questionMapping.getBahmniValue(), answer, questionMapping.getDataTypeHint()));
                    }
                }
            }
        });
        return updatedObservations;

    }

    private List<OpenMRSSaveObservation> voidedObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations, MappingMetaDataCollection conceptMappings) {
        List<OpenMRSSaveObservation> voidedObservations = new ArrayList<>();
        openMRSObservations.forEach(openMRSObservation -> {
            String avniConceptName = conceptMappings.getAvniValueForBahmniValue(openMRSObservation.getConceptUuid());
            Object avniObsValue = avniObservations.get(avniConceptName);

            if (avniObsValue == null) {
                voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid(), openMRSObservation.getConceptUuid()));
            } else {
                if (avniObsValue instanceof List<?>) {
                    List<String> valueList = (List<String>) avniObsValue;
                    String avniAnswerName = conceptMappings.getAvniValueForBahmniValue((String) openMRSObservation.getValue());
                    if (!valueList.contains(avniAnswerName)) {
                        voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid(), openMRSObservation.getConceptUuid()));
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