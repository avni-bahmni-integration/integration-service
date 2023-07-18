package org.avni_integration_service.goonj.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.GoonjMappingGroup;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.repository.DispatchReceiptRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DispatchReceiptWorker extends GeneralEncounterWorker {
    private final DispatchReceiptRepository dispatchReceiptRepository;
    @Autowired
    public DispatchReceiptWorker(AvniEncounterRepository avniEncounterRepository,
                                 AvniSubjectRepository avniSubjectRepository,
                                 AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                                 AvniGoonjErrorService avniGoonjErrorService,
                                 IntegratingEntityStatusRepository integrationEntityStatusRepository,
                                 DispatchReceiptRepository dispatchReceiptRepository,
                                 ErrorClassifier errorClassifier, GoonjContextProvider goonjContextProvider) {
        super(avniEncounterRepository, avniSubjectRepository, avniIgnoredConceptsRepository,
                avniGoonjErrorService, integrationEntityStatusRepository,
                GoonjErrorType.DispatchReceiptAttributesMismatch, GoonjEntityType.DispatchReceipt, Logger.getLogger(DispatchReceiptWorker.class),
                errorClassifier, goonjContextProvider);
        this.dispatchReceiptRepository = dispatchReceiptRepository;
    }
    public void process() throws Exception {
        processEncounters();
    }
    @Override
    protected void createOrUpdateGeneralEncounter(GeneralEncounter generalEncounter, Subject subject) {
        processDispatchReceiptEvent(generalEncounter, subject);
    }
    private void processDispatchReceiptEvent(GeneralEncounter generalEncounter, Subject subject) {
        syncEncounterToGoonj(subject, generalEncounter, dispatchReceiptRepository, "DispatchReceivedStatusId");
    }
}
