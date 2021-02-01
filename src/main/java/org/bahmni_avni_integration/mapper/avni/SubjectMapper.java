package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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

        MappingMetaDataCollection conceptMappings = mappingMetaDataRepository.findAll(MappingGroup.PatientSubject, List.of(MappingType.Concept, MappingType.SubjectUUIDConcept));
        MappingMetaDataCollection auditConceptMappings = mappingMetaDataRepository.findAll(MappingGroup.Audit, MappingType.Concept);

        LinkedHashMap<String, Object> avniObservations = (LinkedHashMap<String, Object>) subject.get("observations");
        avniObservations.forEach((key, value) -> {
            openMRSEncounter.addObservation(new OpenMRSSaveObservation(conceptMappings.getBahmniValueForAvniValue(key), subject.getRegistrationDate(), (String) value, null));
        });
//        openMRSEncounter.add("obs", openMRSObservations);
        return openMRSEncounter;
    }
}