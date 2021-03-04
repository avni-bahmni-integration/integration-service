package org.bahmni_avni_integration.worker.bahmni.atomfeedworker;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.MultipleResultsFoundException;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.PatientService;
import org.bahmni_avni_integration.service.SubjectService;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientEventWorker implements EventWorker {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PatientService patientService;

    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Autowired
    private SubjectService subjectService;
    private Constants constants;

    @Override
    public void process(Event event) {
        OpenMRSPatient patient = patientService.getPatient(event);
        if (patient == null) {
            logger.warn(String.format("Feed out of sync with the actual data: %s", event.toString()));
            return;
        }
        if (patientService.shouldFilterPatient(patient, constants)) {
            logger.warn(String.format("Patient should be filtered out: %s", patient.getPatientId()));
            return;
        }

        logger.debug(String.format("Processing patient: name %s || uuid %s", patient.getName(), patient.getUuid()));
        PatientToSubjectMetaData metaData = mappingMetaDataService.getForPatientToSubject();
        GeneralEncounter patientEncounter = subjectService.findPatient(metaData, patient.getUuid());
        Subject subject;
        try {
            subject = subjectService.findSubject(patient, metaData, constants);
        } catch (MultipleResultsFoundException e) {
            subjectService.processMultipleSubjectsFound(patient);
            return;
        }

        if (patientEncounter != null && subject != null) {
            subjectService.updateRegistrationEncounter(patientEncounter, patient, metaData);
        } else if (patientEncounter != null && subject == null) {
            subjectService.processSubjectIdChanged(patient);
        } else if (patientEncounter == null && subject != null) {
            subjectService.createRegistrationEncounter(patient, subject, metaData);
        } else if (patientEncounter == null && subject == null) {
            subjectService.processSubjectNotFound(patient);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }

//    avoid loading of constants for every event
    public void setConstants(Constants constants) {
        this.constants = constants;
    }
}
