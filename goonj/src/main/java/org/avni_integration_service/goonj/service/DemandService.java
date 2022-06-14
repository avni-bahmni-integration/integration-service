package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.repository.DemandRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Demand;

@Service
public class DemandService extends BaseGoonjService {
    private final DemandRepository demandRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;

    @Autowired
    public DemandService(DemandRepository demandRepositoryGoonj, AvniGoonjErrorService avniGoonjErrorService, MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        super(mappingMetaDataRepository, integrationSystemRepository);
        this.demandRepository = demandRepositoryGoonj;
        this.avniGoonjErrorService = avniGoonjErrorService;
    }

    public HashMap<String, Object> getDemand(String uuid) {
        return demandRepository.getDemand(uuid);
    }

    public void populateObservations(Subject subject , Demand demand) {
        populateObservations(subject, demand, MappingGroup_Demand);
    }
}
