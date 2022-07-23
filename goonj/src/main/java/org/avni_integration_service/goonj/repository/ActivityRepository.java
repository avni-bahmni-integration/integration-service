package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.ActivityConstants;
import org.avni_integration_service.goonj.dto.Activity;
import org.avni_integration_service.goonj.dto.ActivityRequestDTO;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component("ActivityRepository")
public class ActivityRepository extends GoonjBaseRepository implements ActivityConstants {



    @Autowired
    public ActivityRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                              @Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(integratingEntityStatusRepository, restTemplate,
                goonjConfig, GoonjEntityType.Activity.name());
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
    private Activity createActivityRequest(Subject subject, GeneralEncounter encounter) {
        Activity activity = new Activity();
        activity.setSourceId(encounter.getUuid());
        activity.setActivityCategory((String) encounter.getObservation(ACTIVITY_CATEGORY));
        activity.setActivityType((String) encounter.getObservation(ACTIVITY_TYPE));
        Date activityEndDate = DateTimeUtil.convertToDate((String) (String) encounter.getObservation(ACTIVITY_END_DATE));
        activityEndDate = DateTimeUtil.offsetTimeZone(activityEndDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        activity.setActivityEndDate(DateTimeUtil.formatDate(activityEndDate));
        Date activityStartDate = DateTimeUtil.convertToDate((String) (String) encounter.getObservation(ACTIVITY_START_DATE));
        activityStartDate = DateTimeUtil.offsetTimeZone(activityStartDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        activity.setActivityStartDate(DateTimeUtil.formatDate(activityStartDate));
        activity.setActivitySubType((String) encounter.getObservation(ACTIVITY_SUB_TYPE));
        activity.setActivityConductedWithStudents((String) encounter.getObservation(S_2_S_RELATED_ACTIVITY));
        Object activityDistribution = encounter.getObservation(ACTIVITY_S_DISTRIBUTION);
        if(activityDistribution != null) {
            activity.setDistribution((String) activityDistribution);
        } else {
            activity.setDemand(encounter.getSubjectExternalID());
        }
        HashMap<String, String> location = (HashMap<String, String>) encounter.getObservations().get(LOCATION);
        activity.setLocalityVillageName((String) location.get(VILLAGE));
        activity.setBlock((String) location.get(BLOCK));
        activity.setDistrict((String) location.get(DISTRICT));
        activity.setState((String) location.get(STATE));
        activity.setBreadth((encounter.getObservation(BREADTH)==null)?0l:(Integer) encounter.getObservation(BREADTH));
        activity.setDiameter((encounter.getObservation(DIAMETER)==null)?0l:(Integer) encounter.getObservation(DIAMETER));
        activity.setLength((encounter.getObservation(LENGTH)==null)?0l:(Integer) encounter.getObservation(LENGTH));
        activity.setDepthHeight((encounter.getObservation(HEIGHT_DEPTH)==null)?0l:(Integer) encounter.getObservation(HEIGHT_DEPTH));
        activity.setMeasurementType((String) encounter.getObservation(MEASUREMENTS_TYPE));
        Long nos = ((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE))
                + ((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setNos(nos);
        activity.setNoofparticipantsNJPC(nos);
        activity.setNoofparticipantsS2S(nos);
        activity.setNoofdaysofParticipationNJPC((encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activity.setNoofdaysofParticipationS2S((encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activity.setNoofWorkingDays((encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activity.setNoofparticipantsFemaleDFW((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activity.setNoofparticipantsFemaleNJPC((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activity.setNoofparticipantsFemaleS2S((encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activity.setNoofparticipantsMaleDFW((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setNoofparticipantsMaleNJPC((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setNoofparticipantsMaleS2S((encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE)==null)?0l:(Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setObjectiveofDFWwork((String) encounter.getObservation(OBJECTIVE_OF_WORK));
        activity.setOtherObjective((String) encounter.getObservation(SPECIFY_OTHER_FOR_OBJECTIVE_OF_WORK));
        activity.setOtherSubType((String) encounter.getObservation(SPECIFY_OTHER_SUB_TYPE));
        activity.setSchoolAanganwadiLearningCenterName((String) encounter.getObservation(NAME_OF_ORGANIZATION_SCHOOL));
        activity.setTargetCommunity((String) subject.getObservation(TARGET_COMMUNITY));
        activity.setTypeofInitiative((String) encounter.getObservation(TYPE_OF_INITIATIVE));
        return activity;
    }
}