package org.avni_integration_service.goonj.repository;

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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.*;

@Component("ActivityRepository")
public class ActivityRepository extends GoonjBaseRepository implements ActivityConstants {

    protected static final Logger logger = LoggerFactory.getLogger(ActivityRepository.class);
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystem integrationSystem;

    @Autowired
    public ActivityRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                              @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig,
                              MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Activity.name());
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
        ActivityRequestDTO requestDTO = convertGeneralEncounterToActivityRequest(subject, encounter);
        HttpEntity<ActivityRequestDTO> request = new HttpEntity<>(requestDTO);
        return super.createSingleEntity(RESOURCE_ACTIVITY, request);
    }
    private ActivityRequestDTO convertGeneralEncounterToActivityRequest(Subject subject, GeneralEncounter encounter) {
        ActivityRequestDTO requestDTO = new ActivityRequestDTO();
        requestDTO.setActivities(Arrays.asList(createActivityRequest(subject, encounter)));
        return requestDTO;
    }
    private ActivityDTO createActivityRequest(Subject subject, GeneralEncounter encounter) {
        ActivityDTO activityDTO = new ActivityDTO();
        /* Activity ID and relationship fields */
        activityDTO.setSourceId(encounter.getUuid());
        Object activityDistribution = encounter.getObservation(ACTIVITY_S_DISTRIBUTION);
        if(activityDistribution != null) {
            activityDTO.setDistribution((String) activityDistribution);
        } else {
            activityDTO.setDemand(encounter.getSubjectExternalID());
        }
        /* Activity Date fields */
        Date activityEndDate = DateTimeUtil.convertToDate((String) (String) encounter.getObservation(ACTIVITY_END_DATE));
        activityEndDate = DateTimeUtil.offsetTimeZone(activityEndDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        activityDTO.setActivityEndDate(DateTimeUtil.formatDate(activityEndDate));
        Date activityStartDate = DateTimeUtil.convertToDate((String) (String) encounter.getObservation(ACTIVITY_START_DATE));
        activityStartDate = DateTimeUtil.offsetTimeZone(activityStartDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        activityDTO.setActivityStartDate(DateTimeUtil.formatDate(activityStartDate));
        /* Activity location fields */
        HashMap<String, String> location = (HashMap<String, String>) encounter.getObservations().get(LOCATION);
        activityDTO.setLocalityVillageName((String) location.get(VILLAGE));
        activityDTO.setBlock((String) location.get(BLOCK));
        activityDTO.setDistrict((String) location.get(DISTRICT));
        activityDTO.setState((String) location.get(STATE));
        /* Activity description fields */
        activityDTO.setTargetCommunity((String) subject.getObservation(TARGET_COMMUNITY));
        activityDTO.setTypeofInitiative((String) encounter.getObservation(TYPE_OF_INITIATIVE));
        mapActivityType(activityDTO, encounter);
        activityDTO.setActivitySubType((String) encounter.getObservation(ACTIVITY_SUB_TYPE));
        activityDTO.setOtherSubType((String) encounter.getObservation(SPECIFY_OTHER_SUB_TYPE));
        activityDTO.setActivityCategory((String) encounter.getObservation(ACTIVITY_CATEGORY));
        activityDTO.setObjectiveofDFWwork((String) encounter.getObservation(OBJECTIVE_OF_WORK));
        activityDTO.setOtherObjective((String) encounter.getObservation(SPECIFY_OTHER_FOR_OBJECTIVE_OF_WORK));
        activityDTO.setActivityConductedWithStudents((String) encounter.getObservation(S_2_S_RELATED_ACTIVITY));
        activityDTO.setSchoolAanganwadiLearningCenterName((String) encounter.getObservation(NAME_OF_ORGANIZATION_SCHOOL));
        /* Participation fields */
        Long nos = ((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE))
                + ((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activityDTO.setNos(nos);
        activityDTO.setNoofparticipantsNJPC(nos);
        activityDTO.setNoofparticipantsS2S(nos);
        activityDTO.setNoofdaysofParticipationNJPC((encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activityDTO.setNoofdaysofParticipationS2S((encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activityDTO.setNoofWorkingDays((encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activityDTO.setNoofparticipantsFemaleDFW((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activityDTO.setNoofparticipantsFemaleNJPC((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activityDTO.setNoofparticipantsFemaleS2S((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activityDTO.setNoofparticipantsMaleDFW((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activityDTO.setNoofparticipantsMaleNJPC((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activityDTO.setNoofparticipantsMaleS2S((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        /* Measurement fields */
        activityDTO.setBreadth((encounter.getObservation(BREADTH)==null)?0l:(Integer) encounter.getObservation(BREADTH));
        activityDTO.setDiameter((encounter.getObservation(DIAMETER)==null)?0l:(Integer) encounter.getObservation(DIAMETER));
        activityDTO.setLength((encounter.getObservation(LENGTH)==null)?0l:(Integer) encounter.getObservation(LENGTH));
        activityDTO.setDepthHeight((encounter.getObservation(HEIGHT_DEPTH)==null)?0l:(Integer) encounter.getObservation(HEIGHT_DEPTH));
        activityDTO.setMeasurementType((String) encounter.getObservation(MEASUREMENTS_TYPE));
        return activityDTO;
    }

    protected void mapActivityType(ActivityDTO activityDTO, AvniBaseContract encounter) {
        if(encounter.getObservation(ACTIVITY_TYPE) != null) {
            MappingMetaData answerMapping = mappingMetaDataRepository.getIntSystemMappingIfPresent(MappingGroup_Activity, MappingType_Obs,
                    (String) encounter.getObservation(ACTIVITY_TYPE), integrationSystem);
            if (answerMapping != null) {
                activityDTO.setActivityType(answerMapping.getIntSystemValue());
            }
        }
    }
}