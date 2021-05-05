package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation.createVoidedObs;

@Component
public class ObservationMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;

    public ObservationMapper(MappingMetaDataRepository mappingMetaDataRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public List<OpenMRSSaveObservation> updateOpenMRSObservationsFromAvniObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations, List<String> hardcodedConcepts) {
        List<OpenMRSSaveObservation> updateObservations = new ArrayList<>();
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        updateObservations.addAll(voidedObservations(openMRSObservations, avniObservations, conceptMappings, hardcodedConcepts));
        updateObservations.addAll(updatedObservations(openMRSObservations, avniObservations, conceptMappings));
        for (var hardcodedConcept : hardcodedConcepts) {
            Optional<OpenMRSObservation> hardCodedObs = openMRSObservations.stream().filter(o -> o.getConceptUuid().equals(hardcodedConcept)).findFirst();
            hardCodedObs.ifPresent(existing -> {
                OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
                openMRSSaveObservation.setUuid(existing.getObsUuid());
                openMRSSaveObservation.setConcept(existing.getConceptUuid());
                openMRSSaveObservation.setValue(existing.getValue());
                updateObservations.add(openMRSSaveObservation);
            });
        }
        return updateObservations;
    }

    private List<OpenMRSSaveObservation> updatedObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations, MappingMetaDataCollection conceptMappings) {
        List<OpenMRSSaveObservation> updatedObservations = new ArrayList<>();
        avniObservations.forEach((question, answer) -> {
            MappingMetaData questionMapping = conceptMappings.getMappingForAvniValue(question);
            if (questionMapping != null) {
                if (questionMapping.isCoded()) {
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

    private List<OpenMRSSaveObservation> voidedObservations(List<OpenMRSObservation> openMRSObservations, Map<String, Object> avniObservations, MappingMetaDataCollection conceptMappings, List<String> exclude) {
        List<OpenMRSSaveObservation> voidedObservations = new ArrayList<>();
        openMRSObservations.stream().filter(o -> !exclude.contains(o.getConceptUuid())).forEach(openMRSObservation -> {
            MappingMetaData questionMapping = conceptMappings.getMappingForBahmniValue(openMRSObservation.getConceptUuid());
            String avniConceptName = questionMapping.getAvniValue();
            Object avniObsValue = avniObservations.get(avniConceptName);

            if (avniObsValue == null) {
                voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid(), openMRSObservation.getConceptUuid()));
            } else if (questionMapping.isCoded()) {
                if (avniObsValue instanceof List<?>) {
                    List<String> avniObsValueList = (List<String>) avniObsValue;
                    String openMRSAnswerName = conceptMappings.getAvniValueForBahmniValue((String) openMRSObservation.getValue());
                    if (!avniObsValueList.contains(openMRSAnswerName)) {
                        voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid(), openMRSObservation.getConceptUuid()));
                    }
                } else if (avniObsValue instanceof String) {
                    String avniObsValueString = (String) avniObsValue;
                    String openMRSAnswerName = conceptMappings.getAvniValueForBahmniValue((String) openMRSObservation.getValue());
                    if (!avniObsValueString.equals(openMRSAnswerName)) {
                        voidedObservations.add(createVoidedObs(openMRSObservation.getObsUuid(), openMRSObservation.getConceptUuid()));
                    }
                }
            }
        });
        return voidedObservations;
    }

    public List<OpenMRSSaveObservation> mapObservations(LinkedHashMap<String, Object> avniObservations) {
        List<OpenMRSSaveObservation> openMRSObservations = new ArrayList<>();
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        avniObservations.forEach((key, value) -> {
            MappingMetaData questionMapping = conceptMappings.getMappingForAvniValue(key);
            if (questionMapping != null) {
                if (questionMapping.isCoded()) {
                    if (value instanceof String) {
                        MappingMetaData answerMapping = conceptMappings.getMappingForAvniValue((String) value);
                        openMRSObservations.add(OpenMRSSaveObservation.createCodedObs(questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                    } else if (value instanceof List<?>) {
                        List<String> valueList = (List<String>) value;
                        valueList.forEach(s -> {
                            MappingMetaData answerMapping = conceptMappings.getMappingForAvniValue(s);
                            openMRSObservations.add(OpenMRSSaveObservation.createCodedObs(questionMapping.getBahmniValue(), answerMapping.getBahmniValue()));
                        });
                    }
                } else {
                    openMRSObservations.add(OpenMRSSaveObservation.createPrimitiveObs(questionMapping.getBahmniValue(), value, questionMapping.getDataTypeHint()));
                }
            }
        });
        return openMRSObservations;
    }

}