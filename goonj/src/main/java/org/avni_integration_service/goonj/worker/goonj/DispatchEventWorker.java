package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.service.DispatchService;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DispatchEventWorker extends GoonjEventWorker implements ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(DispatchEventWorker.class);

    private final DispatchService dispatchService;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final AvniEncounterRepository avniEncounterRepository;

    @Autowired
    public DispatchEventWorker(DispatchService dispatchService, AvniGoonjErrorService avniGoonjErrorService,
                               AvniEncounterRepository avniEncounterRepository, IntegratingEntityStatusRepository integratingEntityStatusRepository) {
        super(integratingEntityStatusRepository);
        this.dispatchService = dispatchService;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.avniEncounterRepository = avniEncounterRepository;
    }

    public void process(Map<String, Object> event) {
        processDispatch(event);
        updateLastDateTime("Dispatch", event);
    }

    private void processDispatch(Map<String, Object> dispatchResponse) {
        logger.debug(String.format("Processing dispatch: name %s || uuid %s", dispatchResponse.get("DispatchName"), dispatchResponse.get("DispatchId")));
        Dispatch dispatch = Dispatch.from(dispatchResponse);
        GeneralEncounter encounter = dispatch.mapToAvniEncounter();
        dispatchService.populateObservations(encounter, dispatch);
        avniEncounterRepository.create(encounter);
    }

    public void processError(String dispatchUuid) {
        HashMap<String, Object> dispatch = dispatchService.getDispatch(dispatchUuid);
        if (dispatch == null) {
            logger.warn(String.format("Dispatch has been deleted now: %s", dispatchUuid));
            //TODO
//            avniEncounterRepository.dispatchDeleted(dispatchUuid);
            return;
        }
        processDispatch(dispatch);
    }

    @Override
    public void cacheRunImmutables(Constants constants) {
    }
}
