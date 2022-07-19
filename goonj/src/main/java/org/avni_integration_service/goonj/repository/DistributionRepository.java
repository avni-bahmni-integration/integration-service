package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.DistributionConstants;
import org.avni_integration_service.goonj.dto.Distribution;
import org.avni_integration_service.goonj.dto.DistributionLine;
import org.avni_integration_service.goonj.dto.DistributionRequestDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.avni_integration_service.goonj.domain.DispatchReceivedStatusLineItemConstants.*;

@Component("DistributionRepository")
public class DistributionRepository extends GoonjBaseRepository implements DistributionConstants {

    @Autowired
    public DistributionRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                  @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Distribution.name());
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
    private Distribution createDistributionRequest(Subject subject, GeneralEncounter encounter) {
        Distribution distribution = new Distribution();
        distribution.setBlock((String) encounter.getObservation(BLOCK));//TODO Set this info correctly
        distribution.setDispatchStatus((String) encounter.getObservation(DISPATCH_STATUS_ID));//TODO set this or Demand and never both
        distribution.setDistrict((String) encounter.getObservation(DISTRICT)); //TODO Set this and Block info correctly
        distribution.setLocalityVillageName((String) encounter.getObservation(VILLAGE));
        distribution.setSourceId(encounter.getUuid());
        distribution.setState((String) encounter.getObservation(STATE)); //TODO UUID being returned in encounters response
        distribution.setTypeofInitiative((String) encounter.getObservation(TYPE_OF_INITIATIVE));
        distribution.setDateOfDistribution((String) encounter.getObservation(DISTRIBUTION_DATE));
        distribution.setDisasterType((String) subject.getObservation(TYPE_OF_DISASTER));
        distribution.setDuplicate(false); //TODO always false.?
        distribution.setNameofAccount((String) subject.getObservation(ACCOUNT_NAME));
        distribution.setRemarks((String) encounter.getObservation(REMARKS));
        String photoInfo = (String) encounter.getObservation(IMAGES);
        String picStatus = StringUtils.hasText(photoInfo) ? RECEIVED : NOT_RECEIVED;
        distribution.setPictureStatus(picStatus);
        distribution.setPOCId((String) encounter.getObservation(POC_ID));
        distribution.setPhotographInformation(photoInfo);
        distribution.setTypeofCommunity((String) encounter.getObservation(TARGET_COMMUNITY));
        distribution.setDistributionLines(fetchDistributionLines(subject, encounter));
        return distribution;
    }

    private List<DistributionLine> fetchDistributionLines(Subject subject, GeneralEncounter encounter) {
        ArrayList<HashMap<String, Object>> md = (ArrayList<HashMap<String, Object>>) encounter.getObservations().get(MATERIALS);
        return md.stream().map(entry -> createDistributionLine(subject, encounter, entry)).collect( Collectors.toList());
    }

    private DistributionLine createDistributionLine(Subject subject, GeneralEncounter encounter, HashMap<String, Object> entry) {
        //TODO Check mapping and availability of DistributionLineitems values
        String typeOfMaterial = (String) entry.get(TYPE_OF_MATERIAL);
        String recordType = typeOfMaterial.equals(KIT)? KIT_DISPATCH : NON_KIT_MATERIAL_DISPATCH;
        String sourceId = encounter.getUuid()+ DISTRIBUTION_LI_NAME_CONNECTOR + entry.get(DISPATCH_STATUS_LINE_ITEM_ID); //TODO does this work.?
        String kitLineItem = sourceId; //TODO is this correct.?
        String materialInventory = sourceId; //TODO is this correct.?
        String kit = (String) entry.get(KIT_NAME); //TODO is this available.?
        String contributedItem = (String) entry.get(CONTRIBUTED_ITEM_NAME);
        String materialName =  (String) entry.get(PURCHASED_ITEM_NAME);
        String distributedTo = (String) entry.get(DISTRIBUTION_DONE_TO);
        String unit = (String) entry.get(UNIT);
        long noOfDistributions = ((Integer) entry.get(NUMBER_OF_DISTRIBUTIONS));
        long eliQuantity = ((Integer) entry.get(QUANTITY));
        long kitQuantity = ((Integer) entry.get(QUANTITY));
        long quantity = ((Integer) entry.get(QUANTITY));
        return new DistributionLine(sourceId, recordType, contributedItem, distributedTo, eliQuantity,
                kit, kitLineItem, kitQuantity, materialInventory, materialName, noOfDistributions, quantity, unit);
    }
}
