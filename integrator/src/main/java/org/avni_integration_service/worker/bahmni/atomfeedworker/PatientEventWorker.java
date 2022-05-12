package org.avni_integration_service.worker.bahmni.atomfeedworker;

import com.bugsnag.Bugsnag;
import org.avni_integration_service.contract.avni.GeneralEncounter;
import org.avni_integration_service.contract.avni.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.integration_data.BahmniEntityType;
import org.avni_integration_service.integration_data.internal.PatientToSubjectMetaData;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.MultipleResultsFoundException;
import org.avni_integration_service.service.ErrorService;
import org.avni_integration_service.service.MappingMetaDataService;
import org.avni_integration_service.service.PatientService;
import org.avni_integration_service.service.SubjectService;
import org.avni_integration_service.worker.ErrorRecordWorker;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.log4j.Logger;

@Component
public class PatientEventWorker implements EventWorker, ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(PatientEventWorker.class);

    @Autowired
    private PatientService patientService;

    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private SubjectService subjectService;

    private Constants constants;
    private PatientToSubjectMetaData metaData;

    @Value("${app.first.run}")
    private boolean isFirstRun;

    @Autowired
    private Bugsnag bugsnag;

    @Override
    public void process(Event event) {
        try {
            OpenMRSPatient patient = patientService.getPatient(event);
            if (patient == null) {
                logger.warn(String.format("Feed out of sync with the actual data: %s", event.toString()));
                return;
            }
            if (patientService.shouldFilterPatient(patient, constants)) {
                logger.warn(String.format("Patient should be filtered out: %s", patient.getPatientId()));
                return;
            }

            processPatient(patient);
        } catch (Exception e) {
            //            Since atom feed client doesn't throw the exception back to the scheduled job, notify bugsnag here
            bugsnag.notify(e);
            throw e;
        }
    }

    private void processPatient(OpenMRSPatient patient) {
        logger.debug(String.format("Processing patient: name %s || uuid %s", patient.getName(), patient.getUuid()));

        GeneralEncounter patientEncounter = subjectService.findPatient(metaData, patient.getUuid());
        if (isFirstRun) {
            if (patientEncounter != null || errorService.hasError(patient.getUuid(), BahmniEntityType.Patient)) {
                logger.info("Early return for first run, as the record is already processed before");
                return;
            }
        }

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

    public void processError(String patientUuid) {
        OpenMRSPatient patient = patientService.getPatient(patientUuid);
        if (patient == null) {
            logger.warn(String.format("Patient has been deleted now: %s", patientUuid));
            patientService.patientDeleted(patientUuid);
            return;
        }

        if (patientService.shouldFilterPatient(patient, constants)) {
            logger.warn(String.format("Patient is not eligible for integration anymore: %s", patient.getPatientId()));
            patientService.notACommunityMember(patient);
            return;
        }

        processPatient(patient);
    }

    //    avoid loading of constants for every event
    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForPatientToSubject();
    }
}
