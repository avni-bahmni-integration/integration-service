package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniEncounter;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BahmniEncounterService {
    @Autowired
    private OpenMRSEncounterRepository encounterRepository;

    public BahmniEncounter getEncounter(Event event, BahmniEncounterToAvniEncounterMetaData metaData) {
        OpenMRSFullEncounter encounter = encounterRepository.getEncounter(event);
        if (encounter == null) return null;
        return new BahmniEncounter(encounter, metaData);
    }
}