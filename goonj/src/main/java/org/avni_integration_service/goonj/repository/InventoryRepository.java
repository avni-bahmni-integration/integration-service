package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.dto.InventoryResponseDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component("InventoryRepository")
public class InventoryRepository extends GoonjBaseRepository {
    @Autowired
    public InventoryRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository, @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, AvniHttpClient avniHttpClient, GoonjContextProvider goonjContextProvider) {
        super(integratingEntityStatusRepository, restTemplate, GoonjEntityType.Inventory.name(), avniHttpClient, goonjContextProvider);
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return getInventoryItemsDTOS(getCutOffDateTime()).getInventoryItemsDTOS();
    }

    @Override
    public List<String> fetchDeletionEvents() {
        return Arrays.stream(getInventoryItemsDTOS(getCutOffDateTime()).getDeletedItemsDTOS()).filter(obj -> obj instanceof String).map(obj -> (String) obj).collect(Collectors.toList());
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        throw new UnsupportedOperationException();
    }

    public InventoryResponseDTO getInventoryItemsDTOS(Date dateTime) {
        return super.getResponse(dateTime, "ImplementationInventoryService/getImplementationInventories", InventoryResponseDTO.class, "dateTimeStamp");
    }

    public HashMap<String, Object> getInventoryItemsDTO(String uuid) {
        InventoryResponseDTO response = super.getSingleEntityResponse("ImplementationInventoryService/getImplementationInventories", "inventoryId", uuid, InventoryResponseDTO.class);
        return response.getInventoryItemsDTOS()[0];
    }
}
