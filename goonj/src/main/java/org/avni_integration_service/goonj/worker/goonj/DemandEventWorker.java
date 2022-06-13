package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.service.DemandService;
import org.avni_integration_service.integration_data.domain.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DemandEventWorker implements IGoonjEventWorker, ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(DemandEventWorker.class);

    @Autowired
    private DemandService demandService;

    @Autowired
    private AvniGoonjErrorService avniGoonjErrorService;

    @Autowired
    private AvniSubjectRepository avniSubjectRepository;

    private Constants constants;


    @Value("${goonj.app.first.run}")
    private boolean isFirstRun;

    public void process(Map<String, Object> event) {
        processDemand(event);
    }

    private void processDemand(Map<String, Object> demand) {
        logger.debug(String.format("Processing demand: name %s || uuid %s", demand.get("DemandName"), demand.get("DemandId")));
        avniSubjectRepository.create(Demand.subjectFrom(Demand.from(demand)));
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

    //    avoid loading of constants for every event
    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        //TODO
//        metaData = mappingMetaDataService.getForDemandToSubject();
    }
}
