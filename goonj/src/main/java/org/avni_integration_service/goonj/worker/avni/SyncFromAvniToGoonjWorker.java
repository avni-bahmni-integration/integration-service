package org.avni_integration_service.goonj.worker.avni;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.GoonjMappingGroup;
import org.avni_integration_service.goonj.repository.ActivityRepository;
import org.avni_integration_service.goonj.repository.DispatchReceiptRepository;
import org.avni_integration_service.goonj.repository.DistributionRepository;
import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SyncFromAvniToGoonjWorker extends GeneralEncounterWorker {
    private final GoonjMappingGroup goonjMappingGroup;
    private final DispatchReceiptRepository dispatchReceiptRepository;
    private final DistributionRepository distributionRepository;
    private final ActivityRepository activityRepository;
    private final Logger logger = LoggerFactory.getLogger(SyncFromAvniToGoonjWorker.class);
    @Autowired
    public SyncFromAvniToGoonjWorker(AvniEncounterRepository avniEncounterRepository,
                                     AvniSubjectRepository avniSubjectRepository,
                                     AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                     AvniGoonjErrorService avniGoonjErrorService,
                                     IntegratingEntityStatusRepository integrationEntityStatusRepository,
                                     GoonjMappingGroup goonjMappingGroup,
                                     DispatchReceiptRepository dispatchReceiptRepository,
                                     DistributionRepository distributionRepository,
                                     ActivityRepository activityRepository) {
        super(avniEncounterRepository, avniSubjectRepository, avniIgnoredConceptsRepository,
                avniGoonjErrorService, goonjMappingGroup, integrationEntityStatusRepository);
        this.goonjMappingGroup = goonjMappingGroup;
        this.dispatchReceiptRepository = dispatchReceiptRepository;
        this.distributionRepository = distributionRepository;
        this.activityRepository = activityRepository;
    }
    public void process() {
        processEncounters();
    }
    @Override
    protected void createOrUpdateGeneralEncounter(GeneralEncounter generalEncounter, Subject subject) {
        if (goonjMappingGroup.dispatchReceipt.getName().equals(generalEncounter.getEncounterType())) {
            processDispatchReceiptEvent(generalEncounter, subject);
        } else if (goonjMappingGroup.activity.getName().equals(generalEncounter.getEncounterType())) {
            //TODO Remove after impl changes
            throw new NotYetImplementedException();
//            processActivityEvent(generalEncounter, subject);
        } else  if (goonjMappingGroup.distribution.getName().equals(generalEncounter.getEncounterType())) {
            //TODO Remove after impl changes
            throw new NotYetImplementedException();
//            processDistributionEvent(generalEncounter, subject);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    private void processActivityEvent(GeneralEncounter generalEncounter, Subject subject) {
        syncEncounterToGoonj(subject, generalEncounter, activityRepository, "ActivityId");
    }
    private void processDispatchReceiptEvent(GeneralEncounter generalEncounter, Subject subject) {
        syncEncounterToGoonj(subject, generalEncounter, dispatchReceiptRepository, "DispatchReceivedStatusId");
    }
    private void processDistributionEvent(GeneralEncounter generalEncounter, Subject subject) {
        syncEncounterToGoonj(subject, generalEncounter, distributionRepository, "DistributionId");
    }

    private void syncEncounterToGoonj(Subject subject, GeneralEncounter generalEncounter, GoonjBaseRepository repository, String encounterTypeId) {
        HashMap<String, Object>[] response = repository.createEvent(subject, generalEncounter);
        if(repository.wasEventCreatedSuccessfully(response)) {
            logger.debug(String.format("%s %s synced successfully. ", encounterTypeId, response[0].get(encounterTypeId)));
        } else {
            logger.error(String.format("Failed to sync %s with uuid %s ", encounterTypeId, generalEncounter.getUuid()));
        }
    }
}