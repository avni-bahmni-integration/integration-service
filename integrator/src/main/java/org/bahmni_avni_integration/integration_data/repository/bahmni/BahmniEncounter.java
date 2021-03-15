package org.bahmni_avni_integration.integration_data.repository.bahmni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//OpenMRS encounter can be used as general encounter, program enrolment, or program encounter
public class BahmniEncounter {
    private OpenMRSFullEncounter openMRSEncounter;
    private BahmniEncounterToAvniEncounterMetaData metaData;

    public BahmniEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        this.openMRSEncounter = openMRSEncounter;
        this.metaData = metaData;
    }

    public List<BahmniSplitEncounter> getSplitEncounters() {
        List<String> forms = openMRSEncounter.getForms();
        return forms.stream().filter(form -> metaData.hasBahmniConceptSet(form)).map(form -> new BahmniSplitEncounter(form, openMRSEncounter.getUuid(), openMRSEncounter.getEncounterDatetime(), openMRSEncounter.getLeafObservations(form))).collect(Collectors.toList());
    }

    public OpenMRSFullEncounter getOpenMRSEncounter() {
        return openMRSEncounter;
    }
}