package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.dto.DemandsResponseDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public List<String> fetchDeletionEvents() {
        return getDemands(getCutOffDateTime()).getDeletedDemands();
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        throw new UnsupportedOperationException();
    }

    public DemandsResponseDTO getDemands(Date dateTime) {
        return super.getResponse( dateTime, "DemandService/getDemands", DemandsResponseDTO.class);
    }

    public HashMap<String, Object> getDemand(String uuid) {
        DemandsResponseDTO response = super.getSingleEntityResponse("DemandService/getDemand", "demandId", uuid, DemandsResponseDTO.class);
        return response.getDemands()[0];
    }
}
