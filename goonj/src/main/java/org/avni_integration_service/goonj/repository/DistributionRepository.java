package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.DistributionConstants;
import org.avni_integration_service.goonj.dto.DistributionDTO;
import org.avni_integration_service.goonj.dto.DistributionLine;
import org.avni_integration_service.goonj.dto.DistributionRequestDTO;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.avni_integration_service.goonj.domain.DispatchReceivedStatusLineItemConstants.*;

@Component("DistributionRepository")
public class DistributionRepository extends GoonjBaseRepository implements DistributionConstants {

    public static final String WEB_MEDIA = "/web/media";

    @Autowired
    public DistributionRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                  @Qualifier("GoonjRestTemplate") RestTemplate restTemplate,
                                  GoonjConfig goonjConfig, AvniHttpClient avniHttpClient) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Distribution.name(), avniHttpClient);
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
        DistributionRequestDTO requestDTO = convertGeneralEncounterToDistributionRequest(subject, encounter);
        HttpEntity<DistributionRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity(RESOURCE_DISTRIBUTION, request);
    }
    private DistributionRequestDTO convertGeneralEncounterToDistributionRequest(Subject subject, GeneralEncounter encounter) {
        DistributionRequestDTO requestDTO = new DistributionRequestDTO();
        requestDTO.setDistributions(Arrays.asList(createDistributionRequest(subject, encounter)));
        return requestDTO;
    }
    private DistributionDTO createDistributionRequest(Subject subject, GeneralEncounter encounter) {
        DistributionDTO distributionDTO = new DistributionDTO();
        String dispatchStatusId = (String) encounter.getObservation(DISPATCH_STATUS_ID_NEW);
        if(!StringUtils.hasText(dispatchStatusId)) {
            dispatchStatusId = (String) encounter.getObservation(DISPATCH_STATUS_ID_OLD);
        }
        distributionDTO.setDispatchStatus(dispatchStatusId);
        HashMap<String, String> location = (HashMap<String, String>) encounter.getObservations().get(LOCATION);
        distributionDTO.setLocalityVillageName((String) location.get(VILLAGE));
        distributionDTO.setBlock((String) location.get(BLOCK));
        distributionDTO.setDistrict((String) location.get(DISTRICT));
        distributionDTO.setState((String) location.get(STATE));
        distributionDTO.setSourceId(encounter.getUuid());
        distributionDTO.setTypeofInitiative((String) encounter.getObservation(TYPE_OF_INITIATIVE));
        Date distributionDate = DateTimeUtil.convertToDate((String) encounter.getObservation(DISTRIBUTION_DATE));
        distributionDate = DateTimeUtil.offsetTimeZone(distributionDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        distributionDTO.setDateOfDistribution(DateTimeUtil.formatDate(distributionDate));
        distributionDTO.setDisasterType((String) encounter.getObservation(TYPE_OF_DISASTER));
        distributionDTO.setNameofAccount((String) subject.getObservation(ACCOUNT_ID));
        distributionDTO.setRemarks((String) encounter.getObservation(REMARKS));
        String photoInfo = (String) encounter.getObservation(IMAGES);
        distributionDTO.setPictureStatus(StringUtils.hasText(photoInfo) ? RECEIVED : NOT_RECEIVED);
        distributionDTO.setPhotographInformation(StringUtils.hasText(photoInfo) ? getPicSignedUrl(photoInfo):null);
        distributionDTO.setPOCId((String) encounter.getObservation(POC_ID));
        distributionDTO.setTypeofCommunity((String) encounter.getObservation(TARGET_COMMUNITY));
        distributionDTO.setDistributionLines(fetchDistributionLines(subject, encounter));
        ArrayList<String> relatedActivities = (ArrayList<String>) encounter.getObservation(ACTIVITIES_DONE);
        if(relatedActivities == null) {
            relatedActivities = new ArrayList<>();
        }
        distributionDTO.setActivityIds(relatedActivities);
        distributionDTO.setCreatedBy(encounter.getCreatedBy());
        distributionDTO.setModifiedBy(encounter.getLastModifiedBy());
        return distributionDTO;
    }

    private String getPicSignedUrl(String photoInfo) {
        HashMap<String, String> queryParams = new HashMap<>(1);
        queryParams.put("url", photoInfo);
        return avniHttpClient.getUri(WEB_MEDIA, queryParams);
    }


    private List<DistributionLine> fetchDistributionLines(Subject subject, GeneralEncounter encounter) {
        ArrayList<HashMap<String, Object>> md = (ArrayList<HashMap<String, Object>>) encounter.getObservations().get(MATERIALS);
        return md.stream().filter(entry -> entry.get(QUANTITY) != null  && ((Integer) entry.get(QUANTITY)) > 0)
                .map(entry -> createDistributionLine(subject, encounter, entry)).collect( Collectors.toList());
    }

    private DistributionLine createDistributionLine(Subject subject, GeneralEncounter encounter, HashMap<String, Object> entry) {
        String typeOfMaterial = (String) entry.get(TYPE_OF_MATERIAL);
        String recordType = typeOfMaterial.equals(KIT)? KIT_DISPATCH : NON_KIT_MATERIAL_DISPATCH;
        String sourceId = getSourceId(encounter.getUuid(), (String) entry.get(DISPATCH_STATUS_LINE_ITEM_ID));
        String kit = typeOfMaterial.equals(KIT) && StringUtils.hasText((String) entry.get(KIT_ID))? (String) entry.get(KIT_ID) : EMPTY_STRING;
        String materialInventory = !typeOfMaterial.equals(KIT)? (String) entry.get(MATERIAL_ID) : EMPTY_STRING;
        String contributedItem = !typeOfMaterial.equals(KIT)?(String) entry.get(CONTRIBUTED_ITEM_NAME):EMPTY_STRING;
        String distributedTo = !typeOfMaterial.equals(KIT)?(String) entry.get(DISTRIBUTION_DONE_TO):EMPTY_STRING;
        String unit = !typeOfMaterial.equals(KIT) && StringUtils.hasText((String) entry.get(UNIT))?(String) entry.get(UNIT):EMPTY_STRING;
        Long noOfDistributions = !typeOfMaterial.equals(KIT) && entry.get(NUMBER_OF_DISTRIBUTIONS) != null ? ((Integer) entry.get(NUMBER_OF_DISTRIBUTIONS)):0l;
        Long kitQuantity = typeOfMaterial.equals(KIT) && entry.get(QUANTITY) != null ? ((Integer) entry.get(QUANTITY)):0l;
        Long quantity = !typeOfMaterial.equals(KIT) && entry.get(QUANTITY) != null ? ((Integer) entry.get(QUANTITY)):0l;
        return new DistributionLine(sourceId, recordType, contributedItem, distributedTo, null,
                kit, EMPTY_STRING, kitQuantity, materialInventory, EMPTY_STRING, noOfDistributions, quantity, unit);
    }

    public String getSourceId(String encounterUUID, String dispatchLineItemId) {
        String sourceId = encounterUUID+ DISTRIBUTION_LI_NAME_CONNECTOR +dispatchLineItemId ;
        return sourceId;
    }
}
