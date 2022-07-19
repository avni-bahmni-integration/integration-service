package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.ActivityConstants;
import org.avni_integration_service.goonj.dto.Activity;
import org.avni_integration_service.goonj.dto.ActivityRequestDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
        activity.setActivityCategory((String) encounter.getObservation(ACTIVITY_CATEGORY));
        activity.setActivityType((String) encounter.getObservation(ACTIVITY_TYPE));
        activity.setActivityEndDate((String) encounter.getObservation(ACTIVITY_END_DATE));
        activity.setActivityStartDate((String) encounter.getObservation(ACTIVITY_START_DATE));
        activity.setActivitySubType((String) encounter.getObservation(ACTIVITY_SUB_TYPE));
        activity.setActivityConductedWithStudents((String) encounter.getObservation(S_2_S_RELATED_ACTIVITY));
        activity.setBlock((String) encounter.getObservation(BLOCK));//TODO Set this info correctly
        activity.setBreadth((Integer) encounter.getObservation(BREADTH));
        activity.setDemand(encounter.getSubjectExternalID());
        activity.setDiameter((Integer) encounter.getObservation(DIAMETER));
        activity.setDistribution((String) encounter.getObservation(DISTRIBUTION_ID));//TODO set this or Demand and never both
        activity.setDistrict((String) encounter.getObservation(DISTRICT)); //TODO Set this and Block info correctly
        activity.setLength((Integer) encounter.getObservation(LENGTH));
        activity.setDepthHeight((Integer) encounter.getObservation(HEIGHT_DEPTH));
        activity.setMeasurementType((String) encounter.getObservation(MEASUREMENTS_TYPE));
        activity.setLocalityVillageName((String) encounter.getObservation(VILLAGE));
        int nos = (Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION)
                + (Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE);
        activity.setNos(nos);
        activity.setNoofdaysofParticipationNJPC((Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activity.setNoofdaysofParticipationS2S((Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activity.setNoofparticipantsFemaleDFW((Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activity.setNoofparticipantsFemaleNJPC((Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activity.setNoofparticipantsFemaleS2S((Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_FEMALE));
        activity.setNoofparticipantsMaleDFW((Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setNoofparticipantsMaleNJPC((Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setNoofparticipantsMaleS2S((Integer) encounter.getObservation(NUMBER_OF_PARTICIPANTS_MALE));
        activity.setNoofWorkingDays((Integer) encounter.getObservation(NUMBER_OF_DAYS_OF_PARTICIPATION));
        activity.setNoofparticipantsNJPC(nos);
        activity.setNoofparticipantsS2S(nos);
        activity.setObjectiveofDFWwork((String) encounter.getObservation(OBJECTIVE_OF_WORK));
        activity.setOtherObjective((String) encounter.getObservation(SPECIFY_OTHER_FOR_OBJECTIVE_OF_WORK));
        activity.setOtherSubType((String) encounter.getObservation(SPECIFY_OTHER_SUB_TYPE));
        activity.setSchoolAanganwadiLearningCenterName((String) encounter.getObservation(NAME_OF_ORGANIZATION_SCHOOL));
        activity.setSourceId(encounter.getUuid());
        activity.setState((String) encounter.getObservation(STATE)); //TODO UUID being returned in encounters response
        activity.setTargetCommunity((String) subject.getObservation(TARGET_COMMUNITY));
        activity.setTypeofInitiative((String) encounter.getObservation(TYPE_OF_INITIATIVE));
        return activity;
    }
}