package org.bahmni_avni_integration.worker;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Date;
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
    private AvniSubjectRepository avniSubjectRepository;

    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Value("${openmrs.uri.prefix}")
    private String urlPrefix;

    @Override
    public void process(Event event) {
        OpenMRSPatient openMRSPatient = patientRepository.getPatient(event);
        String name = openMRSPatient.getName();
        String uuid = openMRSPatient.getUuid();
        logger.info(String.format("Patient: name %s || uuid %s", name, uuid));


        PatientToSubjectMetaData metaData = mappingMetaDataService.getForPatientToSubject();

        HashMap<String, Object> concepts = new HashMap<>();
        concepts.put(metaData.patientUuidConcept(), openMRSPatient.getUuid());
        Encounter encounter = avniEncounterRepository.getEncounter(lastModifiedDateTime(), concepts);
        if(encounter == null) {

        }
    }

    private Date lastModifiedDateTime() {
        GregorianCalendar calendar = new GregorianCalendar(1900, 0, 1);
        return calendar.getTime();
    }

    @Override
    public void cleanUp(Event event) {
    }
}
