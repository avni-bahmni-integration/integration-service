package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation.*;

@Component
public class SubjectMapper {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public OpenMRSEncounter mapSubjectToEncounter(Subject subject, String patientUuid, String encounterTypeUuid, Constants constants) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterDatetime(FormatAndParseUtil.toISODateStringWithTimezone(new Date()));
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));

        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        mapObservations((LinkedHashMap<String, Object>) subject.get("observations"), openMRSEncounter);
        mapSubjectUuid(subject, openMRSEncounter);
//        story-todo - map audit observations
        LinkedHashMap<String, Object> avniAuditObservations = (LinkedHashMap<String, Object>) subject.get("audit");
        return openMRSEncounter;
    }

    public OpenMRSEncounter mapEnrolmentToEncounter(Enrolment enrolment, String patientUuid, String encounterTypeUuid, Constants constants) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterDatetime(FormatAndParseUtil.toISODateStringWithTimezone(new Date()));
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        mapEnrolmentUuid(enrolment, openMRSEncounter);
        mapObservations((LinkedHashMap<String, Object>) enrolment.get("observations"), openMRSEncounter);
        return openMRSEncounter;
    }

    public List<OpenMRSSaveObservation> mapEnrolmentToExistingEncounter(OpenMRSFullEncounter openMRSEncounter, Enrolment enrolment) {
        Map<String, Object> updateEncounter = new LinkedHashMap<>();
        List<OpenMRSSaveObservation> updateObservations = new ArrayList<>();
        Map<String, Object> avniObservations = (LinkedHashMap<String, Object>) enrolment.get("observations");
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        updateObservations.addAll(voidedObservations(openMRSEncounter, avniObservations, conceptMappings));
        return updateObservations;
    }

    private List<OpenMRSSaveObservation> voidedObservations(OpenMRSFullEncounter openMRSEncounter, Map<String, Object> avniObservations, MappingMetaDataCollection conceptMappings) {
        List<OpenMRSSaveObservation> voidedObservations = new ArrayList<>();
        openMRSEncounter.getLeafObservations().forEach(openMRSObservation -> {
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

    private void mapEnrolmentUuid(Enrolment enrolment, OpenMRSEncounter openMRSEncounter) {
        MappingMetaData enrolmentUuidConcept = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.ProgramEnrolment, MappingType.EnrolmentUUID_Concept);
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(enrolmentUuidConcept.getBahmniValue(), enrolment.getUuid(), ObsDataType.Text));
    }

    private void mapObservations(LinkedHashMap<String, Object> avniObservations, OpenMRSEncounter openMRSEncounter) {
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

    private void mapSubjectUuid(Subject subject, OpenMRSEncounter openMRSEncounter) {
        MappingMetaData subjectUuidMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(subjectUuidMapping.getBahmniValue(), (String) subject.getUuid(), ObsDataType.Text));
    }
}