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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component("DispatchReceiptRepository")
public class DispatchReceiptRepository extends GoonjBaseRepository {
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
        drsDTO.setSourceId(encounter.getExternalID());
        drsDTO.setDispatchStatusId((String) encounter.getObservation("Dispatch Status Id"));
        drsDTO.setReceivedDate(DateTimeUtil.formatDate(encounter.getEncounterDateTime()));
        drsDTO.setDispatchReceivedStatusLineItems(fetchDrsLineItemsFromEncounter(encounter));
        requestDTO.setDispatchReceivedStatus(Arrays.asList(drsDTO));
        return requestDTO;
    }
    private List<DispatchReceivedStatusLineItem> fetchDrsLineItemsFromEncounter(GeneralEncounter encounter) {
        HashMap<String, Object>[] md = (HashMap<String, Object>[]) encounter.getObservations().get("Materials Dispatched");
        return Arrays.stream(md).map(entry -> new DispatchReceivedStatusLineItem(entry)).collect(Collectors.toList());
    }
}
