package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.DistributionConstants;
import org.avni_integration_service.goonj.dto.DistributionActivities;
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
    private AvniSubjectRepository avniSubjectRepository;

    @Autowired
    public DistributionRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                  @Qualifier("GoonjRestTemplate") RestTemplate restTemplate,
                                  GoonjConfig goonjConfig, AvniHttpClient avniHttpClient,
                                  AvniSubjectRepository avniSubjectRepository) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Distribution.name(), avniHttpClient);
        this.avniSubjectRepository = avniSubjectRepository;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject) {
        DistributionRequestDTO requestDTO = convertSubjectToDistributionRequest(subject);
        HttpEntity<DistributionRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity(RESOURCE_DISTRIBUTION, request);
    }

    private DistributionRequestDTO convertSubjectToDistributionRequest(Subject subject) {
        DistributionRequestDTO requestDTO = new DistributionRequestDTO();
        requestDTO.setDistributions(Arrays.asList(createDistributionRequest(subject)));
        return requestDTO;
    }
    private DistributionDTO createDistributionRequest(Subject subject) {
        DistributionDTO distributionDTO = new DistributionDTO();
//        String dispatchStatusId = (String) encounter.getObservation(DISPATCH_STATUS_ID_NEW);
//        if(!StringUtils.hasText(dispatchStatusId)) {
//            dispatchStatusId = (String) encounter.getObservation(DISPATCH_STATUS_ID_OLD);
//        }
//        distributionDTO.setDispatchStatus(dispatchStatusId);
        distributionDTO.setSource_id(subject.getUuid());
        /* Distribution location fields */
        HashMap<String, String> location = (HashMap<String, String>) subject.get(LOCATION);
        distributionDTO.setState(location.get(STATE));
        distributionDTO.setDistrict(location.get(DISTRICT));
        distributionDTO.setBlock(location.get(BLOCK));
        distributionDTO.setLocalityVillageName(location.get(VILLAGE));
        distributionDTO.setTolaMohalla(location.get(TOLA_MOHALLA));
        /* Distribution Account fields */
        distributionDTO.setNameOfAccount((String) subject.getObservation(ACCOUNT_NAME));
        /* Distribution Related fields*/
        Date distributionDate = DateTimeUtil.convertToDate((String) subject.getObservation(DISTRIBUTION_DATE));
        distributionDTO.setDateOfDistribution(DateTimeUtil.formatDate(distributionDate));
        distributionDTO.setTypeOfCommunity((String) subject.getObservation(TARGET_COMMUNITY));
        distributionDTO.setTypeOfInitiative((String) subject.getObservation(TYPE_OF_INITIATIVE));
        distributionDTO.setDisasterType((String) subject.getObservation(TYPE_OF_DISASTER));
        List<String> images = subject.getObservation(IMAGES) == null ? new ArrayList<>() : (ArrayList<String>) subject.getObservation(IMAGES);
        distributionDTO.setPhotographInformation(images.stream().map(Object::toString).collect(Collectors.joining(";")));
        List<DistributionLine> d =  new ArrayList<>();
        d.add(createDistributionLine(subject));
        distributionDTO.setDistributionLines(d);
        List<DistributionActivities> activities =  new ArrayList<>();
        activities.add(createDistributionActivities(subject));
        distributionDTO.setActivities(activities);
        /* vaapsi fields */
        if (subject.getObservation(TYPE_OF_INITIATIVE).equals("Vaapsi")) {
            distributionDTO.setSurveyedBy((String) subject.getObservation(SURVEYED_BY));
            distributionDTO.setMonitoredByOrDistributor((String) subject.getObservation(MONITORED_BY_DISTRIBUTOR));
            distributionDTO.setApprovedOrVerifiedBy((String) subject.getObservation(APPROVED_OR_VERIFIED_BY));
            distributionDTO.setTeamOrExternal((String) subject.getObservation(TEAM_OR_EXTERNAL));
            distributionDTO.setNameOfPOC((String) subject.getObservation(POC_NAME));
            distributionDTO.setPocContactNo((String) subject.getObservation(POC_CONTACT_NO));
            distributionDTO.setReachedTo((String) subject.getObservation(REACHED_TO));
            distributionDTO.setAnyOtherDocumentSubmitted((String) subject.getObservation(ANY_OTHER_DOCUMENT_SUBMITTED));
            if (subject.getObservation(REACHED_TO).equals("Individual")) {
                distributionDTO.setName((String) subject.getObservation(NAME));
                distributionDTO.setGender((String) subject.getObservation(GENDER));
                distributionDTO.setFatherMotherName((String) subject.getObservation(FATHER_MOTHER_NAME));
                distributionDTO.setAge((String) subject.getObservation(AGE));
                distributionDTO.setPhoneNumber((String) subject.getObservation(PHONE_NUMBER));
                distributionDTO.setPresentOccupation((String) subject.getObservation(PRESENT_OCCUPATION));
                distributionDTO.setNoOfFamilyMember((String) subject.getObservation(NUMBER_OF_FAMILY_MEMBERS));
                distributionDTO.setMonthlyIncome((String) subject.getObservation(MONTHLY_INCOME));
            }
            if (subject.getObservation(REACHED_TO).equals("Group")) {
                distributionDTO.setGroupName((String) subject.getObservation(GROUP_NAME));
                distributionDTO.setTotalNumberOfReceivers((String) subject.getObservation(NUMBER_OF_RECEIVERS));
            }
        }
        if (subject.getObservation(TYPE_OF_INITIATIVE).equals("Specific Initiative")) {
            distributionDTO.setCentreName((String) subject.getObservation(CENTERS_NAME));
            distributionDTO.setShareABriefProvidedMaterial((String) subject.getObservation(PROVIDED_MATERIAL));
            distributionDTO.setHowtheMaterialMakesaDifference((String) subject.getObservation(MATERIAL_MAKES_DIFFERENCE));
            distributionDTO.setMaterialGivenFor((String) subject.getObservation(MATERIAL_GIVEN_FOR));
            distributionDTO.setNoOfFamiliesReached((String) subject.getObservation(NUMBER_OF_FAMILIES_REACHED));
            distributionDTO.setNoOfIndividualReached((String) subject.getObservation(NUMBER_OF_INDIVIDUALS_REACHED));

        }
        distributionDTO.setReportsCrosschecked((String) subject.getObservation(REPORTS_CROSS_CHECKED));
        distributionDTO.setRemarks((String) subject.getObservation(REMARKS));
        return distributionDTO;
    }

    private DistributionLine createDistributionLine(Subject subject) {
        String implemenationInventoryId = (String) subject.getObservation(INVENTORY_ID);
        Subject inventorySubject = avniSubjectRepository.getSubject(implemenationInventoryId);
        String sourceId = getSourceId(subject.getUuid(), (String) subject.get(DISPATCH_STATUS_LINE_ITEM_ID));
        String distributedTo = (String) subject.getObservation(DISTRIBUTED_TO);
        String unit = (String) subject.getObservation(UNIT);
        int noOfDistributions = (int) subject.getObservation(NUMBER_OF_DISTRIBUTIONS);
        int quantity = (int) subject.getObservation(QUANTITY);
        return new DistributionLine(sourceId, distributedTo, inventorySubject.getExternalId(), noOfDistributions, quantity, unit);
    }
    private DistributionActivities createDistributionActivities(Subject subject) {
        String activityId = (String) subject.getObservation(ACTIVITIES_DONE);
        int numberOfPersons = (int) subject.getObservation(NUMBER_OF_PERSONS);
        return new DistributionActivities(activityId, numberOfPersons);
    }

    public String getSourceId(String subjectUUID, String dispatchLineItemId) {
        String sourceId = subjectUUID+ DISTRIBUTION_LI_NAME_CONNECTOR + dispatchLineItemId ;
        return sourceId;
    }
}
