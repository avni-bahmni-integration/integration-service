package org.bahmni_avni_integration.worker.bahmni.atomfeedworker;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
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

@Component
public class PatientEncounterEventWorker implements EventWorker {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BahmniEncounterService encounterService;
    @Autowired
    private AvniEncounterService avniEncounterService;
    @Autowired
    private AvniEnrolmentService avniEnrolmentService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private AvniProgramEncounterService programEncounterService;
    @Autowired
    private ErrorService errorService;

    private BahmniEncounterToAvniEncounterMetaData metaData;
    private Constants constants;

    @Override
    public void process(Event event) {
        BahmniEncounter bahmniEncounter = encounterService.getEncounter(event, metaData);
        if (bahmniEncounter == null) {
            logger.warn(String.format("Feed out of sync with the actual data: %s", event.toString()));
            return;
        }

        GeneralEncounter avniPatient = subjectService.findPatient(metaData, bahmniEncounter.getOpenMRSEncounter().getPatient().getUuid());
        try {
            if (encounterService.isProcessableLabEncounter(bahmniEncounter, metaData, constants)) {
                processLabEncounter(bahmniEncounter.getOpenMRSEncounter(), metaData, avniPatient);
            } else {
                if (encounterService.isProcessablePrescriptionEncounter(bahmniEncounter, constants)) {
                    processDrugOrderEncounter(bahmniEncounter.getOpenMRSEncounter(), metaData, avniPatient);
                }
                List<BahmniSplitEncounter> splitEncounters = bahmniEncounter.getSplitEncounters();
                for (BahmniSplitEncounter splitEncounter : splitEncounters) {
                    MappingMetaData mapping = metaData.getEncounterMappingFor(splitEncounter.getFormConceptSetUuid());
                    switch (mapping.getMappingGroup()) {
                        case GeneralEncounter -> processGeneralEncounter(splitEncounter, metaData, avniPatient);
                        case ProgramEnrolment -> processProgramEnrolment(splitEncounter, metaData, avniPatient);
                        case ProgramEncounter -> processProgramEncounter(splitEncounter, metaData, avniPatient);
                    }
                }
            }
        } catch (NoSubjectWithIdException e) {
            errorService.errorOccurred(bahmniEncounter.getOpenMRSEncounter(), ErrorType.NoSubjectWithId);
        } catch (SubjectIdChangedException e) {
            errorService.errorOccurred(bahmniEncounter.getOpenMRSEncounter(), ErrorType.SubjectIdChanged);
        }
    }

    private void processDrugOrderEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        GeneralEncounter existingAvniEncounter = avniEncounterService.getDrugOrderGeneralEncounter(openMRSEncounter, metaData);
        if (existingAvniEncounter != null && avniPatient != null) {
            avniEncounterService.updateDrugOrderEncounter(openMRSEncounter, existingAvniEncounter, metaData, avniPatient);
        } else if (existingAvniEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingAvniEncounter == null && avniPatient != null) {
            avniEncounterService.createDrugOrderEncounter(openMRSEncounter, metaData, avniPatient);
        } else if (existingAvniEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processLabEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        GeneralEncounter existingAvniEncounter = avniEncounterService.getLabResultGeneralEncounter(openMRSEncounter, metaData);
        if (existingAvniEncounter != null && avniPatient != null) {
            avniEncounterService.updateLabEncounter(openMRSEncounter, existingAvniEncounter, metaData, avniPatient);
        } else if (existingAvniEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingAvniEncounter == null && avniPatient != null) {
            avniEncounterService.createLabEncounter(openMRSEncounter, metaData, avniPatient);
        } else if (existingAvniEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processProgramEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        ProgramEncounter existingEncounter = programEncounterService.getProgramEncounter(splitEncounter, metaData);
        Enrolment enrolment = avniEnrolmentService.getMatchingEnrolment(avniPatient.getSubjectExternalId(), splitEncounter, metaData);
        if (existingEncounter != null && avniPatient != null) {
            programEncounterService.update(splitEncounter, existingEncounter, metaData, enrolment);
        } else if (existingEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingEncounter == null && avniPatient != null) {
            programEncounterService.create(splitEncounter, metaData, enrolment);
        } else if (existingEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processProgramEnrolment(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        Enrolment existingEnrolment = avniEnrolmentService.getEnrolment(splitEncounter, metaData);
        if (existingEnrolment != null && avniPatient != null) {
            avniEnrolmentService.update(splitEncounter, existingEnrolment, metaData, avniPatient);
        } else if (existingEnrolment != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingEnrolment == null && avniPatient != null) {
            avniEnrolmentService.create(splitEncounter, metaData, avniPatient);
        } else if (existingEnrolment == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processGeneralEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        GeneralEncounter existingAvniEncounter = avniEncounterService.getGeneralEncounter(splitEncounter, metaData);
        if (existingAvniEncounter != null && avniPatient != null) {
            avniEncounterService.update(splitEncounter, existingAvniEncounter, metaData, avniPatient);
        } else if (existingAvniEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingAvniEncounter == null && avniPatient != null) {
            avniEncounterService.create(splitEncounter, metaData, avniPatient);
        } else if (existingAvniEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    @Override
    public void cleanUp(Event event) {
    }

    //    to avoid loading for every event
    public void setMetaData(BahmniEncounterToAvniEncounterMetaData metaData) {
        this.metaData = metaData;
    }

    public void setConstants(Constants constants) {
        this.constants = constants;
    }

    static class SubjectIdChangedException extends Exception {
    }

    static class NoSubjectWithIdException extends Exception {
    }
}