package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Component("DistributionRepository")
public class DistributionRepository extends GoonjBaseRepository {
    @Autowired
    public DistributionRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                  @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Distribution.name());
    }
    @Override
    public HashMap<String, Object>[] fetchEvents() {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<String> fetchDeletionEvents() {
        throw new UnsupportedOperationException();
    }
    @Override
    public HashMap<String, Object>[] createEvent(GeneralEncounter encounter) {
        //TODO implement logic to create DistributionRequestDTO from GeneralEncounter
        throw new NotYetImplementedException();
//        DispatchReceivedStatusRequestDTO requestDTO = convertGeneralEncounterToDispatchReceivedStatusRequest(encounter);
//        HttpEntity<DispatchReceivedStatusRequestDTO> request = new HttpEntity<>(requestDTO);
//        return super.createSingleEntity("DispatchReceivedService/upsertDispatchReceivedStatus", request);
    }
}
