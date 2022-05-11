package org.avni_integration_service.integration_data.repository.bahmni;

import org.avni_integration_service.contract.bahmni.OpenMRSFullEncounter;
import org.avni_integration_service.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;

import java.util.List;
import java.util.stream.Collectors;

//OpenMRS encounter can be used as general encounter, program enrolment, or program encounter
public class BahmniEncounter {
    private final OpenMRSFullEncounter openMRSEncounter;
    private final BahmniEncounterToAvniEncounterMetaData metaData;

    public BahmniEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        this.openMRSEncounter = openMRSEncounter;
        this.metaData = metaData;
    }

    public List<BahmniSplitEncounter> getSplitEncounters() {
        List<String> forms = openMRSEncounter.getForms();
        return forms.stream().filter(metaData::hasBahmniConceptSet).map(form -> new BahmniSplitEncounter(form, openMRSEncounter.getUuid(), openMRSEncounter.getEncounterDatetime(), openMRSEncounter.getLeafObservations(form), openMRSEncounter.isVoided())).collect(Collectors.toList());
    }

    public OpenMRSFullEncounter getOpenMRSEncounter() {
        return openMRSEncounter;
    }

    public String getEncounterTypeUuid() {
        return getOpenMRSEncounter().getEncounterType().getUuid();
    }

    public String getVisitTypeUuid() {
        return openMRSEncounter.getVisitTypeUuid();
    }
}
