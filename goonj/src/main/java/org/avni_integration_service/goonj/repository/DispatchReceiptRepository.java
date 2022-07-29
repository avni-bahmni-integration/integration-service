package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.goonj.domain.DispatchReceiptConstants;
import org.avni_integration_service.goonj.domain.DispatchReceivedStatusLineItemConstants;
import org.avni_integration_service.goonj.dto.DispatchReceivedStatusLineItem;
import org.avni_integration_service.goonj.dto.DispatchReceivedStatusRequestDTO;
import org.avni_integration_service.goonj.dto.DispatchReceivedstatus;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_DispatchReceipt;
import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingType_Obs;

@Component("DispatchReceiptRepository")
public class DispatchReceiptRepository extends GoonjBaseRepository
        implements DispatchReceiptConstants, DispatchReceivedStatusLineItemConstants {

    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystem integrationSystem;

    @Autowired
    public DispatchReceiptRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                     @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig,
                                     MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.DispatchReceipt.name());
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystem = integrationSystemRepository.findByName(GoonjMappingDbConstants.IntSystemName);
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
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        DispatchReceivedStatusRequestDTO requestDTO = convertGeneralEncounterToDispatchReceivedStatusRequest(encounter);
        HttpEntity<DispatchReceivedStatusRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity(RESOURCE_DISPATCH_RECEIVED_STATUS, request);
    }
    private DispatchReceivedStatusRequestDTO convertGeneralEncounterToDispatchReceivedStatusRequest(GeneralEncounter encounter) {
        DispatchReceivedStatusRequestDTO requestDTO = new DispatchReceivedStatusRequestDTO();
        DispatchReceivedstatus drsDTO = new DispatchReceivedstatus();
        drsDTO.setSourceId(encounter.getUuid());
        drsDTO.setDispatchStatusId((String) encounter.getObservation(DISPATCH_STATUS_ID));
        Date dispatchReceivedDate = DateTimeUtil.convertToDate((String) encounter.getObservation(DISPATCH_RECEIVED_DATE));
        dispatchReceivedDate = DateTimeUtil.offsetTimeZone(dispatchReceivedDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        drsDTO.setReceivedDate(DateTimeUtil.formatDate(dispatchReceivedDate));
        drsDTO.setDispatchReceivedStatusLineItems(fetchDrsLineItemsFromEncounter(encounter));
        requestDTO.setDispatchReceivedStatus(Arrays.asList(drsDTO));
        return requestDTO;
    }
    private List<DispatchReceivedStatusLineItem> fetchDrsLineItemsFromEncounter(GeneralEncounter encounter) {
        ArrayList<HashMap<String, Object>> md = (ArrayList<HashMap<String, Object>>) encounter.getObservations().get(RECEIVED_MATERIAL);
        return md.stream().map(entry -> createDispatchReceivedStatusLineItem(entry)).collect(Collectors.toList());
    }

    public DispatchReceivedStatusLineItem createDispatchReceivedStatusLineItem(HashMap<String, Object> entry) {
        String dispatchStatusLineItemId = (String) entry.get(DISPATCH_STATUS_LINE_ITEM_ID);
        String typeOfMaterial = (String) entry.get(TYPE_OF_MATERIAL);
        String itemName = typeOfMaterial.equals(CONTRIBUTED_ITEM)?
                (String) entry.get(CONTRIBUTED_ITEM_NAME):
                (typeOfMaterial.equals(PURCHASED_ITEM) ? (String) entry.get(MATERIAL_NAME) : (String) entry.get(KIT_NAME));
        long receivedQuantity = ((Integer) entry.get(QUANTITY));
        return new DispatchReceivedStatusLineItem(dispatchStatusLineItemId, mapTypeOfMaterial(entry), itemName,
                dispatchStatusLineItemId, EMPTY_STRING, EMPTY_STRING, null, receivedQuantity);
    }

    protected String mapTypeOfMaterial(HashMap<String, Object> encounter) {
        if(encounter.get(TYPE_OF_MATERIAL) != null) {
            MappingMetaData answerMapping = mappingMetaDataRepository.getIntSystemMappingIfPresent(MappingGroup_DispatchReceipt, MappingType_Obs,
                    (String) encounter.get(TYPE_OF_MATERIAL), integrationSystem);
            if (answerMapping != null) {
                return answerMapping.getIntSystemValue();
            }
        }
        throw new RuntimeException("Type of Material not specified or Mapping not found for Dispatch Receipt");
    }
}