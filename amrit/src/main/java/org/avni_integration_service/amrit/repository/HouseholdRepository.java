package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.HouseholdConstants;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.avni.domain.AvniBaseContract;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Household;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.avni_integration_service.amrit.config.AmritMappingDbConstants.*;

@Component("HouseholdRepository")
public class HouseholdRepository extends AmritBaseRepository implements HouseholdConstants {
    private static final Logger logger = Logger.getLogger(HouseholdRepository.class);

    @Autowired
    public HouseholdRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               @Qualifier("AmritRestTemplate") RestTemplate restTemplate,
                               AmritApplicationConfig amritApplicationConfig,
                               MappingMetaDataRepository mappingMetaDataRepository,
                               IntegrationSystemRepository integrationSystemRepository,
                               MappingGroupRepository mappingGroupRepository,
                               MappingTypeRepository mappingTypeRepository) {
        super(integratingEntityStatusRepository, mappingGroupRepository, restTemplate, amritApplicationConfig,
                mappingMetaDataRepository, integrationSystemRepository, mappingTypeRepository, AmritEntityType.Household.name());
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return new HashMap[0];
    }

    @Override
    public <T extends AmritBaseResponse> T createEvent(AvniBaseContract subject, GeneralEncounter encounter, Class<T> returnType) {
        throw new RuntimeException("Invoke createEvent(Household, GeneralEncounter, Class<T>) instead.");
    }

    public <T extends AmritBaseResponse> T createEvent(Household household, GeneralEncounter encounter, Class<T> returnType) {
        return createSingleEntity(amritApplicationConfig.getIdentityApiPrefix() +UPSERT_AMRIT_BENEFICIARY_RESOURCE_PATH,
                new HttpEntity<HashMap<String, Object>[]>(convertToHouseholdUpsertRequest(household)), returnType);
    }

    private HashMap<String, Object>[] convertToHouseholdUpsertRequest(Household household) {
        HashMap<String, Object> householdObs = new HashMap<String, Object>();
        populateObservations(householdObs, household.getGroupSubject(), MappingGroup_Household, MappingType_HouseholdRoot,
                MappingType_HouseholdObservations);
        initMiscFields(household, householdObs);
        logger.debug(String.format("Converting householdObs to household [%s] and [%s]", householdObs, household));
        return new HashMap[]{householdObs};
    }

    private void initMiscFields(Household household, HashMap<String, Object> householdObs) {
        Subject groupSubject = household.getGroupSubject();
        if(StringUtils.hasText(groupSubject.getExternalId())) {
            householdObs.put(BENEFICIARY_REG_ID, household.getMemberSubject().getExternalId());
        }
        householdObs.put(AVNI_BENEFICIARY_ID, household.getMemberSubject().getUuid());
        householdObs.put(AVNI_HOUSEHOLD_ID, household.getGroupSubject().getUuid());
        householdObs.put(VAN_ID, VAN_ID_VALUE);
        householdObs.put(CREATED_BY, household.getCreatedBy());
        householdObs.put(CREATED_DATE, household.getCreateDate());
        householdObs.put(DELETED, household.getVoided());
    }

}
