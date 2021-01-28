package org.bahmni_avni_integration.repository.openmrs;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSEncounterRepository extends BaseOpenMRSRepository {
    public OpenMRSEncounter getEncounterByObservation() {
        return null;
    }
}