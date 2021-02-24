package org.bahmni_avni_integration.mapper.bahmni;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSEncounterMapper {
    public Encounter mapToAvniEncounter(OpenMRSFullEncounter openMRSEncounter) {
        return null;
    }
}