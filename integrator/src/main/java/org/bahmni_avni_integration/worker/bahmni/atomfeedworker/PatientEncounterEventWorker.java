package org.bahmni_avni_integration.worker.bahmni.atomfeedworker;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniEncounter;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.service.*;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PatientEncounterEventWorker implements EventWorker {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BahmniEncounterService encounterService;
    @Autowired
    private AvniEncounterService avniEncounterService;
    @Autowired
    private SubjectService subjectService;

    private BahmniEncounterToAvniEncounterMetaData metaData;

    @Override
    public void process(Event event) {
        BahmniEncounter bahmniEncounter = encounterService.getEncounter(event, metaData);
        if (bahmniEncounter == null) {
            logger.warn(String.format("Feed out of sync with the actual data: %s", event.toString()));
            return;
        }

        GeneralEncounter avniPatient = subjectService.findPatient(metaData, bahmniEncounter.getOpenMRSEncounter().getPatient().getUuid());

        List<BahmniSplitEncounter> splitEncounters = bahmniEncounter.getSplitEncounters();
        splitEncounters.forEach((splitEncounter) -> {
            GeneralEncounter existingAvniEncounter = avniEncounterService.getGeneralEncounter(bahmniEncounter.getOpenMRSEncounter(), metaData);

            if (existingAvniEncounter != null && avniPatient != null) {
                avniEncounterService.update(splitEncounter, existingAvniEncounter, metaData, avniPatient);
            } else if (existingAvniEncounter != null && avniPatient == null) {
                avniEncounterService.processSubjectIdChanged(existingAvniEncounter, metaData);
            } else if (existingAvniEncounter == null && avniPatient != null) {
                avniEncounterService.create(splitEncounter, metaData, avniPatient);
            } else if (existingAvniEncounter == null && avniPatient == null) {
                avniEncounterService.processSubjectIdNotFound(bahmniEncounter.getOpenMRSEncounter());
            }
        });
    }

    @Override
    public void cleanUp(Event event) {
    }

    //    to avoid loading for every event
    public void setMetaData(BahmniEncounterToAvniEncounterMetaData metaData) {
        this.metaData = metaData;
    }

    public void setConstants(Constants constants) {
    }
}