package org.avni_integration_service.goonj.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjMappingGroup;
import org.avni_integration_service.goonj.repository.DistributionRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                              DistributionRepository distributionRepository) {
        super(avniEncounterRepository, avniSubjectRepository, avniIgnoredConceptsRepository,
                avniGoonjErrorService, goonjMappingGroup, integrationEntityStatusRepository,
                GoonjEntityType.Distribution.getDbName(), Logger.getLogger(DistributionWorker.class));
        this.distributionRepository = distributionRepository;

    }
    public void process() {
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