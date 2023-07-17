package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
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

    @Autowired
    public DemandService(DemandRepository demandRepositoryGoonj, MappingMetaDataRepository mappingMetaDataRepository, GoonjContextProvider goonjContextProvider) {
        super(mappingMetaDataRepository, goonjContextProvider);
        this.demandRepository = demandRepositoryGoonj;
    }

    public HashMap<String, Object> getDemand(String uuid) {
        return demandRepository.getDemand(uuid);
    }

    public void populateObservations(Subject subject , Demand demand) {
        populateObservations(subject, demand, MappingGroup_Demand);
    }
}
