package org.bahmni_avni_integration.worker.bahmni.atomfeedworker;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSDefaultEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniEncounter;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.service.*;
import org.bahmni_avni_integration.worker.ErrorRecordWorker;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientEncounterEventWorker implements EventWorker, ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(PatientEncounterEventWorker.class);

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
    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    private BahmniEncounterToAvniEncounterMetaData metaData;
    private Constants constants;

    @Override
    public void process(Event event) {
        if (!"Encounter".equals(event.getTitle())) {
            logger.info(String.format("Found event of title: %s", event.getTitle()));
            return;
        }

        BahmniEncounter bahmniEncounter = encounterService.getEncounter(event, metaData);
        if (bahmniEncounter == null) {
            logger.warn(String.format("Feed out of sync with the actual data: %s", event.toString()));
            return;
        }

        processEncounter(bahmniEncounter);
    }

    private void processEncounter(BahmniEncounter bahmniEncounter) {
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
                        case ProgramEnrolment -> throw new RuntimeException("Cannot map Bahmni Encounter Form to Avni Enrolment");
                        case ProgramEncounter -> processProgramEncounter(splitEncounter, metaData, avniPatient);
                    }
                }
            }
            errorService.successfullyProcessed(bahmniEncounter.getOpenMRSEncounter());
        } catch (NoSubjectWithIdException e) {
            errorService.errorOccurred(bahmniEncounter.getOpenMRSEncounter(), ErrorType.NoSubjectWithId);
            logger.info("No subject found with the identifier");
        } catch (SubjectIdChangedException e) {
            errorService.errorOccurred(bahmniEncounter.getOpenMRSEncounter(), ErrorType.SubjectIdChanged);
            logger.info("Subject id changed!!");
        }
    }

    private void processDrugOrderEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        GeneralEncounter existingAvniEncounter = avniEncounterService.getDrugOrderGeneralEncounter(openMRSEncounter, metaData);
        if (existingAvniEncounter != null && avniPatient != null) {
            OpenMRSDefaultEncounter defaultEncounter = encounterService.getDefaultEncounter(openMRSEncounter.getUuid());
            avniEncounterService.updateDrugOrderEncounter(openMRSEncounter, existingAvniEncounter, metaData, avniPatient, defaultEncounter);
            logger.info("Updated drug order encounter");
        } else if (existingAvniEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingAvniEncounter == null && avniPatient != null) {
            OpenMRSDefaultEncounter defaultEncounter = encounterService.getDefaultEncounter(openMRSEncounter.getUuid());
            avniEncounterService.createDrugOrderEncounter(openMRSEncounter, metaData, avniPatient, defaultEncounter);
            logger.info("Created drug order encounter");
        } else if (existingAvniEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processLabEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        GeneralEncounter existingAvniEncounter = avniEncounterService.getLabResultGeneralEncounter(openMRSEncounter, metaData);
        if (existingAvniEncounter != null && avniPatient != null) {
            avniEncounterService.updateLabEncounter(openMRSEncounter, existingAvniEncounter, metaData, avniPatient);
            logger.info("Updated lab encounter");
        } else if (existingAvniEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingAvniEncounter == null && avniPatient != null) {
            avniEncounterService.createLabEncounter(openMRSEncounter, metaData, avniPatient);
            logger.info("Created lab encounter");
        } else if (existingAvniEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processProgramEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        ProgramEncounter existingEncounter = programEncounterService.getProgramEncounter(splitEncounter, metaData);
        Enrolment enrolment = avniPatient == null ? null : avniEnrolmentService.getMatchingEnrolment(avniPatient.getSubjectId(), splitEncounter, metaData);

        if (existingEncounter != null && avniPatient != null) {
            programEncounterService.update(splitEncounter, existingEncounter, metaData, enrolment);
            logger.info("Updated program encounter");
        } else if (existingEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingEncounter == null && avniPatient != null) {
            Enrolment effectiveEnrolment = enrolIfNotEnrolled(enrolment, metaData, splitEncounter, avniPatient);
            programEncounterService.create(splitEncounter, metaData, effectiveEnrolment);
            logger.info("Created program encounter");
        } else if (existingEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private Enrolment enrolIfNotEnrolled(Enrolment enrolment, BahmniEncounterToAvniEncounterMetaData metaData, BahmniSplitEncounter bahmniSplitEncounter, GeneralEncounter avniPatient) {
        if (enrolment == null) {
            Enrolment newEnrolment = avniEnrolmentService.createEmptyEnrolmentFor(bahmniSplitEncounter, metaData, avniPatient);
            logger.info("Created empty program enrolment");
            return newEnrolment;
        }
        return enrolment;
    }

    private void processProgramEnrolment(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        Enrolment existingEnrolment = avniEnrolmentService.getEnrolment(splitEncounter, avniPatient.getSubjectId(), metaData);
        if (existingEnrolment != null && avniPatient != null) {
            avniEnrolmentService.update(splitEncounter, existingEnrolment, metaData, avniPatient);
            logger.info("Updated program enrolment");
        } else if (existingEnrolment != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingEnrolment == null && avniPatient != null) {
            avniEnrolmentService.create(splitEncounter, metaData, avniPatient);
            logger.info("Created program enrolment");
        } else if (existingEnrolment == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    private void processGeneralEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) throws NoSubjectWithIdException, SubjectIdChangedException {
        GeneralEncounter existingAvniEncounter = avniEncounterService.getGeneralEncounter(splitEncounter, metaData);
        if (existingAvniEncounter != null && avniPatient != null) {
            avniEncounterService.update(splitEncounter, existingAvniEncounter, metaData, avniPatient);
            logger.info("Updated general encounter");
        } else if (existingAvniEncounter != null && avniPatient == null) {
            throw new SubjectIdChangedException();
        } else if (existingAvniEncounter == null && avniPatient != null) {
            avniEncounterService.create(splitEncounter, metaData, avniPatient);
            logger.info("Created general encounter");
        } else if (existingAvniEncounter == null && avniPatient == null) {
            throw new NoSubjectWithIdException();
        }
    }

    @Override
    public void cleanUp(Event event) {
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        this.metaData = mappingMetaDataService.getForBahmniEncounterToAvniEntities();
    }

    @Override
    public void processError(String entityUuid) {
        BahmniEncounter bahmniEncounter = encounterService.getEncounter(entityUuid, metaData);
        if (bahmniEncounter == null) {
            logger.warn(String.format("Encounter has been deleted now: %s", entityUuid));
            errorService.errorOccurred(entityUuid, ErrorType.EntityIsDeleted, BahmniEntityType.Encounter);
            return;
        }

        processEncounter(bahmniEncounter);
    }

    public static class SubjectIdChangedException extends Exception {
    }

    static class NoSubjectWithIdException extends Exception {
    }
}
