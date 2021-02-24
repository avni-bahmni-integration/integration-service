package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class AvniEncounterService {
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;
    @Autowired
    private AvniEncounterRepository avniEncounterRepository;

    public void update(OpenMRSFullEncounter openMRSEncounter) {
        Encounter encounter = openMRSEncounterMapper.mapToAvniEncounter(openMRSEncounter);
    }

    public Encounter getEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put(metaData.getBahmniEntityUuidConcept(), openMRSEncounter.getUuid());
        avniEncounterRepository.getEncounter(encounterCriteria);
        return null;
    }

    public void processSubjectIdChanged() {

    }

    public void create(OpenMRSFullEncounter openMRSEncounter) {

    }

    public void processSubjectIdNotFound() {

    }
}