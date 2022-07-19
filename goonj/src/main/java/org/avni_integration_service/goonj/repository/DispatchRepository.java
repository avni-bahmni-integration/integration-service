package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.dto.DispatchesResponseDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Component("DispatchRepository")
public class DispatchRepository extends GoonjBaseRepository {
    @Autowired
    public DispatchRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                            @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Dispatch.name());
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return getDispatches(getCutOffDateTime()).getDispatchStatuses();
    }

    @Override
    public List<String> fetchDeletionEvents() {
        return getDispatches(getCutOffDateTime()).getDeletedObjects().getDeletedDispatchStatuses();
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        throw new UnsupportedOperationException();
    }

    public DispatchesResponseDTO getDispatches(LocalDateTime dateTime) {
        return super.getResponse( dateTime, "DispatchService/getDispatches", DispatchesResponseDTO.class);
    }

    public HashMap<String, Object> getDispatch(String uuid) {
        return super.getSingleEntityResponse("DispatchService/getDispatch", uuid);
    }
}
