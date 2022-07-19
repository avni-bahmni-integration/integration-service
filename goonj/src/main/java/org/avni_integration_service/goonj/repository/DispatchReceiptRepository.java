package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.dto.DispatchReceivedStatusLineItem;
import org.avni_integration_service.goonj.dto.DispatchReceivedStatusRequestDTO;
import org.avni_integration_service.goonj.dto.DispatchReceivedstatus;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component("DispatchReceiptRepository")
public class DispatchReceiptRepository extends GoonjBaseRepository {

    public static final String YES = "yes";
    @Autowired
    public DispatchReceiptRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                     @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.DispatchReceipt.name());
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
        DispatchReceivedStatusRequestDTO requestDTO = convertGeneralEncounterToDispatchReceivedStatusRequest(encounter);
        HttpEntity<DispatchReceivedStatusRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity("DispatchReceivedService/upsertDispatchReceivedStatus", request);
    }
    private DispatchReceivedStatusRequestDTO convertGeneralEncounterToDispatchReceivedStatusRequest(GeneralEncounter encounter) {
        DispatchReceivedStatusRequestDTO requestDTO = new DispatchReceivedStatusRequestDTO();
        DispatchReceivedstatus drsDTO = new DispatchReceivedstatus();
        drsDTO.setSourceId(encounter.getUuid());
        drsDTO.setDispatchStatusId((String) encounter.getObservation("Dispatch Status Id"));
        Date dispatchReceivedDate = DateTimeUtil.convertToDate((String) encounter.getObservation("Dispatch Received Date"));
        drsDTO.setReceivedDate(DateTimeUtil.formatDate(dispatchReceivedDate));
        drsDTO.setDispatchReceivedStatusLineItems(fetchDrsLineItemsFromEncounter(encounter));
        requestDTO.setDispatchReceivedStatus(Arrays.asList(drsDTO));
        return requestDTO;
    }
    private List<DispatchReceivedStatusLineItem> fetchDrsLineItemsFromEncounter(GeneralEncounter encounter) {
        ArrayList<HashMap<String, Object>> md = (ArrayList<HashMap<String, Object>>) encounter.getObservations().get("Received Material");
        return md.stream().map(entry -> createDispatchReceivedStatusLineItem(entry)).collect(Collectors.toList());
    }

    public DispatchReceivedStatusLineItem createDispatchReceivedStatusLineItem(HashMap<String, Object> entry) {
        //TODO Verify if the DispatchReceivedStatus request lineItems are created as required by Goonj SF application
        String dispatchStatusLineItemId = (String) entry.get("Dispatch Status Line Item Id");
        String typeOfMaterial = (String) entry.get("Type Of Material");
        String itemName = typeOfMaterial.equals("Contributed item")?
                (String) entry.get("Contributed Item"):
                (typeOfMaterial.equals("Purchased item") ? (String) entry.get("Purchased /High Value") : "");
        String unit = (String) entry.get("Unit");
        String receivingStatus = YES.equalsIgnoreCase((String) entry.get("Quantity matching")) ? "" : "recievedPartially";
        long dispatchedQuantity = ((Integer) entry.get("Quantity (Dispatched)"));
        long receivedQuantity = ((Integer) entry.get("Quantity"));
        return new DispatchReceivedStatusLineItem(dispatchStatusLineItemId, typeOfMaterial, itemName,
                unit, receivingStatus, dispatchedQuantity, receivedQuantity);
    }
}
