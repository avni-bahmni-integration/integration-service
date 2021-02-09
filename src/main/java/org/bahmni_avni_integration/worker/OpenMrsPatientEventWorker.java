package org.bahmni_avni_integration.worker;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.GregorianCalendar;
import java.util.HashMap;

@Component
public class OpenMrsPatientEventWorker implements EventWorker {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OpenMRSPatientRepository patientRepository;

    @Autowired
    private AvniEncounterRepository avniEncounterRepository;

    @Autowired
    private OpenMRSWebClient openMRSWebClient;

    @Value("${openmrs.uri.prefix}")
    private String urlPrefix;

    @Override
    public void process(Event event) {
        String content = event.getContent();
        URI uri = URI.create(urlPrefix + content);
        String patientJSON = openMRSWebClient.get(uri);
        OpenMRSPatient openMRSPatient = ObjectJsonMapper.readValue(patientJSON, OpenMRSPatient.class);

        String name = openMRSPatient.getName();
        String uuid = openMRSPatient.getUuid();
        logger.info(String.format("Patient: name %s || uuid %s", name, uuid));

        GregorianCalendar calendar = new GregorianCalendar(1900, 0, 1);
        HashMap<String, Object> concepts = new HashMap<>();
        concepts.put("Last FE of first FEG", "2-1");
        Encounter[] encounters = avniEncounterRepository.getEncounters(calendar.getTime(), concepts);
        logger.info(String.format("Encounters length: %s", encounters.length));
        for (Encounter encounter : encounters) {
            logger.info(String.format("Encounter: %s", encounter));
        }

    }

    @Override
    public void cleanUp(Event event) {
    }
}
