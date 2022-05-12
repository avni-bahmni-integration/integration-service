package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.contract.OpenMRSDefaultEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.avni_integration_service.bahmni.repository.BahmniEncounter;
import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BahmniEncounterService {
    @Autowired
    private OpenMRSEncounterRepository encounterRepository;

    public BahmniEncounter getEncounter(Event event, BahmniEncounterToAvniEncounterMetaData metaData) {
        OpenMRSFullEncounter encounter = encounterRepository.getEncounter(event);
        if (encounter == null) return null;
        return new BahmniEncounter(encounter, metaData);
    }

    public BahmniEncounter getEncounter(String encounterUuid, BahmniEncounterToAvniEncounterMetaData metaData) {
        OpenMRSFullEncounter encounter = encounterRepository.getEncounterByUuid(encounterUuid);
        if (encounter == null) return null;
        return new BahmniEncounter(encounter, metaData);
    }

    public boolean isProcessableLabEncounter(BahmniEncounter bahmniEncounter, BahmniEncounterToAvniEncounterMetaData metaData, Constants constants) {
         return this.isOutpatientEncounter(bahmniEncounter, constants) && bahmniEncounter.getEncounterTypeUuid().equals(metaData.getLabEncounterTypeMapping().getBahmniValue());
    }

    private boolean isOutpatientEncounter(BahmniEncounter bahmniEncounter, Constants constants) {
        List<String> outPatientVisitTypes = constants.getValues(ConstantKey.OutpatientVisitTypes).stream().map(Constant::getValue).collect(Collectors.toList());
        String visitTypeUuid = bahmniEncounter.getVisitTypeUuid();
        long count = outPatientVisitTypes.stream().filter(visitTypeUuid::equals).count();
        return count != 0;
    }

    public boolean isProcessablePrescriptionEncounter(BahmniEncounter bahmniEncounter, Constants constants) {
        return isOutpatientEncounter(bahmniEncounter, constants) && bahmniEncounter.getOpenMRSEncounter().hasDrugOrders();
    }

    public OpenMRSDefaultEncounter getDefaultEncounter(String uuid) {
        return encounterRepository.getDefaultEncounter(uuid);
    }
}
