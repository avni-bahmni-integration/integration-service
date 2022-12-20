package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.service.DemandService;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Component
public class DemandEventWorker extends GoonjEventWorker implements ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(DemandEventWorker.class);
    private final DemandService demandService;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final AvniSubjectRepository avniSubjectRepository;

    @Autowired
    public DemandEventWorker(DemandService demandService, AvniGoonjErrorService avniGoonjErrorService,
                             AvniSubjectRepository avniSubjectRepository, IntegratingEntityStatusRepository integratingEntityStatusRepository,
                             ErrorClassifier errorClassifier, @Qualifier("GoonjIntegrationSystem") IntegrationSystem integrationSystem) {
        super(avniGoonjErrorService, integratingEntityStatusRepository, GoonjEntityType.Demand, errorClassifier, integrationSystem);
        this.demandService = demandService;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.avniSubjectRepository = avniSubjectRepository;
    }

    public void process(Map<String, Object> event) throws Exception {
        try {
            processDemand(event);
            updateErrorRecordAndSyncStatus(event, true, (String) event.get("DemandId"));
        } catch (Exception e) {
            handleError(event, e, "DemandId", GoonjErrorType.DemandAttributesMismatch);
        }
    }

    private void processDemand(Map<String, Object> demandResponse) {
        logger.debug(String.format("Processing demand: name %s || uuid %s", demandResponse.get("DemandName"), demandResponse.get("DemandId")));
        Demand demand = Demand.from(demandResponse);
        Subject subject = demand.subjectWithoutObservations();
        demandService.populateObservations(subject, demand);
        avniSubjectRepository.create(subject);
    }

    public void processError(String demandUuid) throws Exception {
        HashMap<String, Object> demand = demandService.getDemand(demandUuid);
        if (demand == null) {
            logger.warn(String.format("Demand has been deleted now: %s", demandUuid));
            updateErrorRecordAndSyncStatus(null, false, demandUuid);
            return;
        }
        process(demand);
    }

    @Override
    public void processDeletion(String deletedEntity) {
        processDemandDeletion(deletedEntity);
    }

    private void processDemandDeletion(String deletedEntity) {
        try {
            logger.debug(String.format("Processing demand deletion: externalId %s", deletedEntity));
            avniSubjectRepository.delete(deletedEntity);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error(String.format("Failed to delete non-existent demand: externalId %s", deletedEntity));
        } catch (Exception e) {
            logger.error(String.format("Failed to delete demand: externalId %s", deletedEntity));
        }
    }
}
