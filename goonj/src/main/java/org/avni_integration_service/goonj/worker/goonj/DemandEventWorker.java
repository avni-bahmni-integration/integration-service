package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.service.DemandService;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DemandEventWorker implements IGoonjEventWorker, ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(DemandEventWorker.class);

    private final DemandService demandService;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final AvniSubjectRepository avniSubjectRepository;

    @Autowired
    public DemandEventWorker(DemandService demandService, AvniGoonjErrorService avniGoonjErrorService, AvniSubjectRepository avniSubjectRepository) {
        this.demandService = demandService;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.avniSubjectRepository = avniSubjectRepository;
    }

    public void process(Map<String, Object> event) {
        processDemand(event);
    }

    private void processDemand(Map<String, Object> demandResponse) {
        logger.debug(String.format("Processing demand: name %s || uuid %s", demandResponse.get("DemandName"), demandResponse.get("DemandId")));
        Demand demand = Demand.from(demandResponse);
        Subject subject = demand.fromSubject();

        avniSubjectRepository.create(subject);
    }

    public void processError(String demandUuid) {
        HashMap<String, Object> demand = demandService.getDemand(demandUuid);
        if (demand == null) {
            logger.warn(String.format("Demand has been deleted now: %s", demandUuid));
            //TODO
//            avniSubjectRepository.demandDeleted(demandUuid);
            return;
        }

        processDemand(demand);
    }

    @Override
    public void cacheRunImmutables(Constants constants) {
    }
}
