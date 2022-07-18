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
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
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
        if (goonjMappingGroup.activity.getName().equals(generalEncounter.getEncounterType())) {
            processActivityEvent(generalEncounter);
        } else if (goonjMappingGroup.dispatchReceipt.getName().equals(generalEncounter.getEncounterType())) {
            processDispatchReceiptEvent(generalEncounter);
        } else if (goonjMappingGroup.distribution.getName().equals(generalEncounter.getEncounterType())) {
            processDistributionEvent(generalEncounter);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    private void processActivityEvent(GeneralEncounter generalEncounter) {
        HashMap<String, Object>[] response = activityRepository.createEvent(generalEncounter);
        boolean processingStatus = activityRepository.wasEventCreatedSuccessfully(response);
        logger.debug(String.format("ActivityId %s created successfully: %s",
                response[0].get("ActivityId"), processingStatus));
    }
    private void processDispatchReceiptEvent(GeneralEncounter generalEncounter) {
        HashMap<String, Object>[] response = dispatchReceiptRepository.createEvent(generalEncounter);
        boolean processingStatus = dispatchReceiptRepository.wasEventCreatedSuccessfully(response);
        logger.debug(String.format("DispatchReceivedStatusId %s created successfully: %s",
                response[0].get("DispatchReceivedStatusId"), processingStatus));
    }
    private void processDistributionEvent(GeneralEncounter generalEncounter) {
        HashMap<String, Object>[] response = distributionRepository.createEvent(generalEncounter);
        boolean processingStatus = distributionRepository.wasEventCreatedSuccessfully(response);
        logger.debug(String.format("DistributionId %s created successfully: %s",
                response[0].get("DistributionId"), processingStatus));
    }
}