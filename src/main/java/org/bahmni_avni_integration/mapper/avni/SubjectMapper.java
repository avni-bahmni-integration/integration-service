package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;

@Component
public class SubjectMapper {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public OpenMRSEncounter mapSubjectToEncounter(Subject subject, String patientUuid, String encounterTypeUuid) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterDatetime(new Date());
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation("c1e42932-3f10-11e4-adec-0800271c1b75");

        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider("c1c26908-3f10-11e4-adec-0800271c1b7", "0d0c9cdd-ff8c-11e4-b248-005056820298"));

        MappingMetaDataCollection auditConceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Audit, MappingType.Concept);

        mapObservations(subject, openMRSEncounter);
        mapSubjectUuid(subject, openMRSEncounter);
        LinkedHashMap<String, Object> avniAuditObservations = (LinkedHashMap<String, Object>) subject.get("audit");
        return openMRSEncounter;
    }

    private void mapObservations(Subject subject, OpenMRSEncounter openMRSEncounter) {
        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, MappingType.Concept);
        LinkedHashMap<String, Object> avniObservations = (LinkedHashMap<String, Object>) subject.get("observations");
        avniObservations.forEach((key, value) -> {
            MappingMetaData mapping = conceptMappings.getMappingForAvniValue(key);
            if (mapping != null && ObsDataType.Coded.equals(mapping.getObsDataType()))
                openMRSEncounter.addObservation(OpenMRSSaveObservation.createCodedObs(mapping.getBahmniValue(), subject.getRegistrationDate(), (String) value));
            else if (mapping != null)
                openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(mapping.getBahmniValue(), subject.getRegistrationDate(), (String) value));
        });
    }

    private void mapSubjectUuid(Subject subject, OpenMRSEncounter openMRSEncounter) {
        MappingMetaData subjectUuidMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(subjectUuidMapping.getBahmniValue(), subject.getRegistrationDate(), (String) subject.get("ID")));
    }
}