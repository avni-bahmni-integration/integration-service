package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;

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

    private void mapEnrolmentUuid(Enrolment enrolment, OpenMRSEncounter openMRSEncounter) {
        MappingMetaData enrolmentUuidConcept = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.ProgramEnrolment, MappingType.EnrolmentUUID_Concept);
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(enrolmentUuidConcept.getBahmniValue(), enrolment.getUuid(), ObsDataType.Text));
    }

    private void mapObservations(LinkedHashMap<String, Object> avniObservations, OpenMRSEncounter openMRSEncounter) {
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.Concept);
        avniObservations.forEach((key, value) -> {
            MappingMetaData mapping = conceptMappings.getMappingForAvniValue(key);
            if (mapping != null && ObsDataType.Coded.equals(mapping.getDataTypeHint()))
                openMRSEncounter.addObservation(OpenMRSSaveObservation.createCodedObs(mapping.getBahmniValue(), (String) value));
            else if (mapping != null)
                openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(mapping.getBahmniValue(), value, mapping.getDataTypeHint()));
        });
    }

    private void mapSubjectUuid(Subject subject, OpenMRSEncounter openMRSEncounter) {
        MappingMetaData subjectUuidMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(subjectUuidMapping.getBahmniValue(), (String) subject.getUuid(), ObsDataType.Text));
    }
}