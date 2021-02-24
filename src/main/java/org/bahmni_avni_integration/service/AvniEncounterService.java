package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvniEncounterService {
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;

    public void update(OpenMRSFullEncounter openMRSEncounter) {
        Encounter encounter = openMRSEncounterMapper.mapToAvniEncounter(openMRSEncounter);
    }

    public Encounter getEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        return null;
    }

    public void processSubjectIdChanged() {

    }

    public void create(OpenMRSFullEncounter openMRSEncounter) {

    }

    public void processSubjectIdNotFound() {

    }
}