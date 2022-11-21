package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.BeneficiaryConstants;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.avni.domain.AvniBaseContract;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.*;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.avni_integration_service.amrit.config.AmritMappingDbConstants.*;

@Component("BeneficiaryRepository")
public class BeneficiaryRepository extends AmritBaseRepository implements BeneficiaryConstants {
    private static final Logger logger = Logger.getLogger(BeneficiaryRepository.class);
    public static final String DOB = "dOB";

    @Autowired
    public BeneficiaryRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                 @Qualifier("AmritRestTemplate") RestTemplate restTemplate,
                                 AmritApplicationConfig amritApplicationConfig,
                                 MappingMetaDataRepository mappingMetaDataRepository,
                                 IntegrationSystemRepository integrationSystemRepository,
                                 MappingGroupRepository mappingGroupRepository,
                                 MappingTypeRepository mappingTypeRepository) {
        super(integratingEntityStatusRepository, mappingGroupRepository, restTemplate, amritApplicationConfig,
                mappingMetaDataRepository, integrationSystemRepository, mappingTypeRepository, AmritEntityType.Beneficiary.name());
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return new HashMap[0];
    }

    @Override
    public <T extends AmritBaseResponse> T createEvent(AvniBaseContract subject, AvniBaseContract enrolment, Class<T> returnType) {
        return createSingleEntity(amritApplicationConfig.getIdentityApiPrefix() +UPSERT_AMRIT_BENEFICIARY_RESOURCE_PATH,
                new HttpEntity<HashMap<String, Object>[]>(convertToBeneficiaryUpsertRequest((Subject) subject)), returnType);
    }

    private HashMap<String, Object>[] convertToBeneficiaryUpsertRequest(Subject subject) {
        HashMap<String, Object> beneficiary = new HashMap<String, Object>();
        populateObservations(beneficiary, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryRoot,
                MappingType_BeneficiaryObservations);
        initDemographicsFields(subject, beneficiary);
        initPhoneMapFields(subject, beneficiary);
        initIdentityFields(subject, beneficiary);
        initMiscFields(subject, beneficiary);
        logger.debug(String.format("Converting subject to beneficiary [%s] and [%s]", subject, beneficiary));
        return new HashMap[]{beneficiary};
    }

    private void initDemographicsFields(Subject subject, HashMap<String, Object> beneficiary) {
        HashMap<String, Object> demographics = new HashMap<String, Object>();
        populateObservations(demographics, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryDemographics,
                MappingType_BeneficiaryObservations);
        initLocationFields(subject, demographics);
        if(demographics.get(EDUCATION_NAME) != null && StringUtils.hasText((String) demographics.get(EDUCATION_NAME))) {
            beneficiary.put(LITERACY_STATUS, demographics.get(EDUCATION_NAME));
        }
        beneficiary.put(Beneficiary_Demographics_KeyName, demographics);
    }

    private void initPhoneMapFields(Subject subject, HashMap<String, Object> beneficiary) {
        HashMap<String, Object> phoneMaps = new HashMap<String, Object>();
        populateObservations(phoneMaps, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryPhoneMaps,
                MappingType_BeneficiaryObservations);
        HashMap[] phoneMapsArray = (phoneMaps.size() > 0) ? new HashMap[]{phoneMaps} : new HashMap[0];
        beneficiary.put(Beneficiary_PhoneMaps_KeyName, phoneMapsArray);
    }

    private void initIdentityFields(Subject subject, HashMap<String, Object> beneficiary) {
        HashMap<String, Object> beneficiariesIdentity = new HashMap<String, Object>();
        beneficiariesIdentity.put(IDENTITY_TYPE, NATIONAL_ID);
        populateObservations(beneficiariesIdentity, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryIdentity,
                MappingType_BeneficiaryObservations);
        beneficiary.put(Beneficiary_Identities_KeyName, new HashMap[]{beneficiariesIdentity});
    }

    private void initMiscFields(Subject subject, HashMap<String, Object> beneficiary) {
        if(StringUtils.hasText(subject.getExternalId())) {
            beneficiary.put(BENEFICIARY_REG_ID, subject.getExternalId());
        }

        beneficiary.put(DOB, FormatAndParseUtil.fromAvniToOpenMRSDate((String) beneficiary.get(DOB)));
        beneficiary.put(VAN_ID, VAN_ID_VALUE);
        beneficiary.put(CREATED_BY, subject.getCreatedBy());
    }

    private void initLocationFields(Subject subject, HashMap<String, Object> demographics) {
        HashMap<String, String> location = (HashMap<String, String>) subject.get(LOCATION);
        demographics.put(STATE_ID, getValue(location, STATE_EXTERNAL_ID));
        demographics.put(DISTRICT_ID, getValue(location, DISTRICT_EXTERNAL_ID));
        demographics.put(BLOCK_ID, getValue(location, BLOCK_EXTERNAL_ID));
//        demographics.put(PANCHAYAT_ID, Integer.parseInt(location.get(PANCHAYAT_EXTERNAL_ID)));
        demographics.put(DISTRICT_BRANCH_ID, getValue(location, VILLAGE_EXTERNAL_ID));
    }

    private int getValue(HashMap<String, String> location, String externalId) {
        if(!StringUtils.hasText(externalId) || !StringUtils.hasText(location.get(externalId))) {
            throw new AssertionError(String.format("Unable to convert location field of type %s to its id mapping %s",
                    externalId, location.get(externalId)));
        }
        return Integer.parseInt(location.get(externalId));
    }

}
