package org.bahmni_avni_integration.worker.bahmni.atomfeedworker;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.service.*;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMrsEncounterEventWorker implements EventWorker {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BahmniEncounterService encounterService;

    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Autowired
    private AvniEncounterService avniEncounterService;
    @Autowired
    private SubjectService subjectService;

    private Constants constants;
    private BahmniEncounterToAvniEncounterMetaData metaData;

    @Override
    public void process(Event event) {
        OpenMRSFullEncounter openMRSEncounter = encounterService.getEncounter(event);
        if (openMRSEncounter == null) {
            logger.warn(String.format("Feed out of sync with the actual data: %s", event.toString()));
            return;
        }
        if (encounterService.doFilterEncounter(openMRSEncounter, metaData)) {
            return;
        }

        Encounter existingEncounter = avniEncounterService.getEncounter(openMRSEncounter, metaData);
        Encounter patient = subjectService.findPatient(metaData, openMRSEncounter.getPatient().getUuid());
        if (existingEncounter != null && patient != null) {
            avniEncounterService.update(openMRSEncounter);
        } else if (existingEncounter != null && patient == null) {
            avniEncounterService.processSubjectIdChanged();
        } else if (existingEncounter == null && patient != null) {
            avniEncounterService.create(openMRSEncounter);
        } else {
            avniEncounterService.processSubjectIdNotFound();
        }
    }

    @Override
    public void cleanUp(Event event) {
    }

    //    avoid loading of constants for every event
    public void setConstants(Constants constants) {
        this.constants = constants;
    }

    public void setMetaData(BahmniEncounterToAvniEncounterMetaData metaData) {
        this.metaData = metaData;
    }
}
