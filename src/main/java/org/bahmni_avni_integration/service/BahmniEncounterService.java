package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSEncounterRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BahmniEncounterService {
    @Autowired
    private OpenMRSEncounterRepository encounterRepository;

    public OpenMRSFullEncounter getEncounter(Event event) {
        return encounterRepository.getEncounter(event);
    }

    public boolean doFilterEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        return !metaData.hasBahmniEncounterType(openMRSEncounter.getEncounterType().getUuid());
    }
}