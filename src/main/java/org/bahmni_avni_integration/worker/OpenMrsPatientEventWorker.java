package org.bahmni_avni_integration.worker;

import org.bahmni_avni_integration.contract.avni.Encounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.SubjectService;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    private SubjectService subjectService;


    @Value("${openmrs.uri.prefix}")
    private String urlPrefix;

    @Override
    public void process(Event event) {
        OpenMRSPatient openMRSPatient = patientRepository.getPatient(event);
        logger.debug(String.format("Patient: name %s || uuid %s", openMRSPatient.getName(), openMRSPatient.getUuid()));
        PatientToSubjectMetaData patientToSubjectMetaData = mappingMetaDataService.getForPatientToSubject();
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put(patientToSubjectMetaData.patientUuidConcept(), openMRSPatient.getUuid());
        Encounter encounter = avniEncounterRepository.getEncounter(lastModifiedDateTime(), encounterCriteria);
        if(encounter == null) {
            logger.debug("Enc not found");
            Subject subject = subjectService.findSubject(openMRSPatient, patientToSubjectMetaData);
            if(subject != null) {
                Encounter registrationEncounter = subjectService.createRegistrationEncounter(openMRSPatient, subject, patientToSubjectMetaData);
                logger.debug(String.format("New encounter created %s", registrationEncounter));
            } else {
                logger.debug("Subject not found");
            }
        } else {
            Encounter updatedEncounter = subjectService.updateRegistrationEncounter(encounter, openMRSPatient);
            logger.debug(String.format("Encounter updated %s", updatedEncounter));
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
