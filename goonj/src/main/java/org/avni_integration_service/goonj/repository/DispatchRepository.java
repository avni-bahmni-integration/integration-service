package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.dto.DeletedDispatchStatusLineItem;
import org.avni_integration_service.goonj.dto.DispatchesResponseDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component("DispatchRepository")
public class DispatchRepository extends GoonjBaseRepository {
    @Autowired
    public DispatchRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                              @Qualifier("GoonjRestTemplate") RestTemplate restTemplate,
                              AvniHttpClient avniHttpClient, GoonjContextProvider goonjContextProvider) {
        super(integratingEntityStatusRepository, restTemplate, GoonjEntityType.Dispatch.name(), avniHttpClient, goonjContextProvider);
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return getDispatches(getCutOffDateTime()).getDispatchStatuses();
    }

    @Override
    public List<String> fetchDeletionEvents() {
        return getDispatches(getCutOffDateTime()).getDeletedObjects().getDeletedDispatchStatuses();
    }

    public List<DeletedDispatchStatusLineItem> fetchDispatchLineItemDeletionEvents() {
        return getDispatches(getCutOffDateTime()).getDeletedObjects().getDeletedDispatchStatusLineItems();
    }

    public HashMap<String, Object>[] createEvent(Subject subject) {
        throw new UnsupportedOperationException();
    }
    @Override
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        throw new UnsupportedOperationException();
    }

    public DispatchesResponseDTO getDispatches(Date dateTime) {
        return super.getResponse( dateTime, "DispatchService/getDispatches", DispatchesResponseDTO.class, "dateTimestamp");
    }

    public HashMap<String, Object> getDispatch(String uuid) {
        DispatchesResponseDTO response = super.getSingleEntityResponse("DispatchService/getDispatch", "dispatchStatusId", uuid, DispatchesResponseDTO.class);
        return response.getDispatchStatuses()[0];
    }
}
