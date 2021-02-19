package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class AvniEncounterService {
    @Autowired
    private AvniEncounterRepository avniEncounterRepository;

    public Encounter getEncounter(String externalId, PatientToSubjectMetaData metaData) {
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put(metaData.patientUuidConcept(), externalId);
        return avniEncounterRepository.getEncounter(encounterCriteria);
    }
}