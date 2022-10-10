package org.avni_integration_service.goonj.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.client.AvniHttpClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_DispatchReceipt;
import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingType_Obs;

@Component("DispatchReceiptRepository")
public class DispatchReceiptRepository extends GoonjBaseRepository
        implements DispatchReceiptConstants, DispatchReceivedStatusLineItemConstants {
    private static final Logger logger = Logger.getLogger(DispatchReceiptRepository.class);
    private final boolean deleteAndRecreateDispatchReceipt;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystem integrationSystem;

    @Autowired
    public DispatchReceiptRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                     @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig,
                                     MappingMetaDataRepository mappingMetaDataRepository,
                                     IntegrationSystemRepository integrationSystemRepository, AvniHttpClient avniHttpClient,
                                     @Value("${goonj.app.recreate.dispatch.receipt.enabled}") boolean deleteAndRecreateDispatchReceipt) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.DispatchReceipt.name(), avniHttpClient);
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystem = integrationSystemRepository.findByName(GoonjMappingDbConstants.IntSystemName);
        this.deleteAndRecreateDispatchReceipt = deleteAndRecreateDispatchReceipt;
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
        deleteAndRecreateDispatchReceipt(encounter);
        DispatchReceivedStatusRequestDTO requestDTO = convertGeneralEncounterToDispatchReceivedStatusRequest(encounter);
        HttpEntity<DispatchReceivedStatusRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity(RESOURCE_DISPATCH_RECEIVED_STATUS, request);
    }

    private void deleteAndRecreateDispatchReceipt(GeneralEncounter encounter) {
        if(deleteAndRecreateDispatchReceipt) {
            try {
                deleteEvent(RESOURCE_DELETE_DISPATCH_RECEIVED_STATUS, encounter);
            } catch (HttpClientErrorException hce) {
                if (hce.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    logger.info(String.format("Ignoring failure to delete missing DispatchReceipt, %s", encounter.getUuid()));
                    return;
                }
                throw hce; //throw all exceptions, other than NOT_FOUND
            }
        }
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
        drsDTO.setCreatedBy(encounter.getCreatedBy());
        drsDTO.setModifiedBy(encounter.getLastModifiedBy());
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
                (typeOfMaterial.equals(KIT) ? (String) entry.get(KIT_NAME) : (String) entry.get(MATERIAL_NAME));
        long receivedQuantity = entry.get(QUANTITY) != null ? ((Integer) entry.get(QUANTITY)):0l;
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