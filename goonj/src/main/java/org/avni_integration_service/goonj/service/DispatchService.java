package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.repository.DispatchRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Dispatch;


@Service
public class DispatchService extends BaseGoonjService {
    private final DispatchRepository dispatchRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;

    @Autowired
    public DispatchService(DispatchRepository dispatchRepository, AvniGoonjErrorService avniGoonjErrorService, MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        super(mappingMetaDataRepository, integrationSystemRepository);
        this.dispatchRepository = dispatchRepository;
        this.avniGoonjErrorService = avniGoonjErrorService;
    }

    public HashMap<String, Object> getDispatch(String uuid) {
        return dispatchRepository.getDispatch(uuid);
    }

    public void populateObservations(GeneralEncounter encounter, Dispatch dispatch) {
        populateObservations(encounter, dispatch, MappingGroup_Dispatch);
    }
}
