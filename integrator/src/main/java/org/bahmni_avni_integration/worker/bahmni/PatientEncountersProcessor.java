package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;

public interface PatientEncountersProcessor {
    void processEncounters();
}
