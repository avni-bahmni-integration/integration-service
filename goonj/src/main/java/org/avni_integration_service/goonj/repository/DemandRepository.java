package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.dto.DemandsResponseDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component("DemandRepository")
public class DemandRepository extends GoonjBaseRepository {
    @Autowired
    public DemandRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                            @Qualifier("GoonjRestTemplate")RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Demand.name());
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return getDemands(getCutOffDateTime()).getDemands();
    }

    public DemandsResponseDTO getDemands(LocalDateTime dateTime) {
        return super.getResponse( dateTime, "DemandService/getDemands", DemandsResponseDTO.class);
    }

    public HashMap<String, Object> getDemand(String uuid) {
        return super.getSingleEntityResponse("DemandService/getDemand", uuid);
    }
}
