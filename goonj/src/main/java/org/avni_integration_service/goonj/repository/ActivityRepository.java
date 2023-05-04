package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.AvniBaseContract;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.goonj.domain.ActivityConstants;
import org.avni_integration_service.goonj.dto.ActivityDTO;
import org.avni_integration_service.goonj.dto.ActivityRequestDTO;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.*;

@Component("ActivityRepository")
public class ActivityRepository extends GoonjBaseRepository implements ActivityConstants {

    protected static final Logger logger = LoggerFactory.getLogger(ActivityRepository.class);
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystem integrationSystem;

    @Autowired
    public ActivityRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                              @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig,
                              MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository,
                              AvniHttpClient avniHttpClient) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Activity.name(), avniHttpClient);
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
    public HashMap<String, Object>[] createEvent(Subject subject) {
        ActivityRequestDTO requestDTO = convertSubjectToActivityRequest(subject);
        HttpEntity<ActivityRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity(RESOURCE_ACTIVITY, request);
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        throw new UnsupportedOperationException();
    }

    private ActivityRequestDTO convertSubjectToActivityRequest(Subject subject) {
        ActivityRequestDTO requestDTO = new ActivityRequestDTO();
        requestDTO.setActivities(Arrays.asList(createActivityRequest(subject)));
        return requestDTO;
    }

    private ActivityDTO createActivityRequest(Subject subject) {
        ActivityDTO activityDTO = new ActivityDTO();
        /* Activity ID and relationship fields */
        // Todo: Distribution should be mapped!
        activityDTO.setSourceId(subject.getUuid());
        /* Activity location fields */
        HashMap<String, String> location = (HashMap<String, String>) subject.get(LOCATION);
        activityDTO.setState(location.get(STATE));
        activityDTO.setDistrict(location.get(DISTRICT));
        activityDTO.setBlock(location.get(BLOCK));
        activityDTO.setLocalityVillageName(location.get(VILLAGE));
        /* Activity Account fields */
        activityDTO.setnameOfAccount((String) subject.getObservation(ACCOUNT_NAME));
        /* Activity description fields */
        activityDTO.setTypeofInitiative((String) subject.getObservation(TYPE_OF_INITIATIVE));
        /* Activity Date fields */
        Date activityEndDate = DateTimeUtil.convertToDate((String) subject.getObservation(ACTIVITY_END_DATE));
        activityEndDate = DateTimeUtil.offsetTimeZone(activityEndDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        activityDTO.setActivityEndDate(DateTimeUtil.formatDate(activityEndDate));
        Date activityStartDate = DateTimeUtil.convertToDate((String) subject.getObservation(ACTIVITY_START_DATE));
        activityStartDate = DateTimeUtil.offsetTimeZone(activityStartDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        activityDTO.setActivityStartDate(DateTimeUtil.formatDate(activityStartDate));

        activityDTO.setActivityConductedWithStudents((String) subject.getObservation(ACTIVITY_CONDUCTED_WITH_STUDENTS));
        activityDTO.setSchoolAanganwadiLearningCenterName((String) subject.getObservation(SCHOOL_AANGANWADI_LEARNINGCENTER_NAME));

        if (subject.getObservation(TYPE_OF_INITIATIVE).equals("CFW")) {
            /* Participation fields */
            activityDTO.setNoofWorkingDays((subject.getObservation(NUMBER_OF_WORKING_DAYS) == null) ? 0L : (Integer) subject.getObservation(NUMBER_OF_WORKING_DAYS));
            HashMap<String, Integer> noOfParticipants = (HashMap<String, Integer>) subject.getObservation("Number of participants");
            activityDTO.setNoofparticipantsMaleCFW(noOfParticipants.get("526b0d5d-51cc-4004-8c12-7a6c71c6c516"));
            activityDTO.setNoofparticipantsFemaleCFW(noOfParticipants.get("2966afcc-2c07-44cf-8711-3fc23f52a6b5"));
            //activityDTO.setNoofparticipantsMaleCFW((subject.getObservation(NUMBER_OF_PARTICIPANTS_MALE) == null) ? 0L : (Integer) subject.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
            //activityDTO.setNoofparticipantsFemaleCFW((subject.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE) == null) ? 0L : (Integer) subject.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
            /* Activity description fields */
            activityDTO.setActivityCategory((String) subject.getObservation(ACTIVITY_CATEGORY));
            mapActivityType(activityDTO, subject);
            activityDTO.setActivitySubType((String) subject.getObservation(ACTIVITY_SUB_TYPE));
            activityDTO.setOtherSubType((String) subject.getObservation(SPECIFY_OTHER_SUB_TYPE));
            activityDTO.setObjectiveofCFWwork((String) subject.getObservation(OBJECTIVE_OF_WORK));
            activityDTO.setOtherObjective((String) subject.getObservation(SPECIFY_OTHER_FOR_OBJECTIVE_OF_WORK));
            /* Measurement fields */
            activityDTO.setMeasurementType((String) subject.getObservation(MEASUREMENTS_TYPE));
            activityDTO.setNos((Integer) subject.getObservation(NOS));
            activityDTO.setBreadth((subject.getObservation(BREADTH) == null) ? 0L : (Integer) subject.getObservation(BREADTH));
            activityDTO.setDiameter((subject.getObservation(DIAMETER) == null) ? 0L : (Integer) subject.getObservation(DIAMETER));
            activityDTO.setLength((subject.getObservation(LENGTH) == null) ? 0L : (Integer) subject.getObservation(LENGTH));
            activityDTO.setDepthHeight((subject.getObservation(HEIGHT_DEPTH) == null) ? 0L : (Integer) subject.getObservation(HEIGHT_DEPTH));
            /* Photograph fields */
            activityDTO.setBeforeImplementationPhotograph((String) subject.getObservation(BEFORE_IMPLEMENTATION_PHOTOGRAPH));
            activityDTO.setDuringImplementationPhotograph((String) subject.getObservation(DURING_IMPLEMENTATION_PHOTOGRAPH));
            activityDTO.setAfterImplementationPhotograph((String) subject.getObservation(AFTER_IMPLEMENTATION_PHOTOGRAPH));
        }
        if (subject.getObservation(TYPE_OF_INITIATIVE).equals("S2S")) {
            /* Participation fields */
            activityDTO.setNoofparticipantsS2S((subject.getObservation(NUMBER_OF_PARTICIPANTS) == null) ? 0L : (Integer) subject.getObservation(NUMBER_OF_PARTICIPANTS));
            activityDTO.setNoofdaysofParticipationS2S((subject.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION) == null) ? 0L : (Integer) subject.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
            activityDTO.setSchoolAanganwadiLearningCenterName((String) subject.getObservation(SCHOOL_AANGANWADI_LEARNINGCENTER_NAME));
            /* Photograph fields */
            List<String> s2sPhotograph = (ArrayList<String>) subject.getObservation(PHOTOGRAPH);
            activityDTO.setS2sPhotograph(s2sPhotograph.stream().map(Object::toString).collect(Collectors.joining(";")));
            /* Activity description fields */
            activityDTO.setTypeOfSchool((String) subject.getObservation(TYPE_OF_SCHOOL));
        }
        if (subject.getObservation(TYPE_OF_INITIATIVE).equals("NJPC")) {
            /* Participation fields */
            activityDTO.setNoofdaysofParticipationNJPC((subject.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION) == null) ? 0L : (Integer) subject.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
            HashMap<String, Integer> noOfParticipants = (HashMap<String, Integer>) subject.getObservation("Number of participants");
            activityDTO.setNoofparticipantsMaleNJPC(noOfParticipants.get("526b0d5d-51cc-4004-8c12-7a6c71c6c516"));
            activityDTO.setNoofparticipantsFemaleNJPC(noOfParticipants.get("2966afcc-2c07-44cf-8711-3fc23f52a6b5"));
            activityDTO.setNoofparticipantsNJPCOther(noOfParticipants.get("a043fea3-1658-4b5e-becd-ee55ab305a03"));
            /* Photograph fields */
            List<String> njpcPhotographs = (ArrayList<String>) subject.getObservation(PHOTOGRAPH);
            activityDTO.setNjpcPhotograph(njpcPhotographs.stream().map(Object::toString).collect(Collectors.joining(";")));
        }
         /* Other fields */
        activityDTO.setCreatedBy(subject.getCreatedBy());
        activityDTO.setModifiedBy(subject.getLastModifiedBy());
        return activityDTO;
    }

    protected void mapActivityType(ActivityDTO activityDTO, AvniBaseContract subject) {
        if (subject.getObservation(ACTIVITY_TYPE) != null) {
            MappingMetaData answerMapping = mappingMetaDataRepository.getIntSystemMappingIfPresent(MappingGroup_Activity, MappingType_Obs,
                    (String) subject.getObservation(ACTIVITY_TYPE), integrationSystem);
            if (answerMapping != null) {
                activityDTO.setActivityType(answerMapping.getIntSystemValue());
            }
        }
    }
}
