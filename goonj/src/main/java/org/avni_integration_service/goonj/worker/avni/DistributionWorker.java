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
import org.avni_integration_service.goonj.repository.DistributionRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DistributionWorker extends GeneralEncounterWorker {
    private final DistributionRepository distributionRepository;
    @Autowired
    public DistributionWorker(AvniEncounterRepository avniEncounterRepository,
                              AvniSubjectRepository avniSubjectRepository,
                              AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                              AvniGoonjErrorService avniGoonjErrorService,
                              IntegratingEntityStatusRepository integrationEntityStatusRepository,
                              GoonjMappingGroup goonjMappingGroup,
                              DistributionRepository distributionRepository,
                              ErrorClassifier errorClassifier, @Qualifier("GoonjIntegrationSystem") IntegrationSystem integrationSystem) {
        super(avniEncounterRepository, avniSubjectRepository, avniIgnoredConceptsRepository,
                avniGoonjErrorService, goonjMappingGroup, integrationEntityStatusRepository,
                GoonjErrorType.DistributionAttributesMismatch, GoonjEntityType.Distribution, Logger.getLogger(DistributionWorker.class),
                errorClassifier, integrationSystem);
        this.distributionRepository = distributionRepository;

    }
    public void process() throws Exception {
        processEncounters();
    }
    @Override
    protected void createOrUpdateGeneralEncounter(GeneralEncounter generalEncounter, Subject subject) {
        processDistributionEvent(generalEncounter, subject);
    }
    private void processDistributionEvent(GeneralEncounter generalEncounter, Subject subject) {
        syncEncounterToGoonj(subject, generalEncounter, distributionRepository, "DistributionId");
    }
}