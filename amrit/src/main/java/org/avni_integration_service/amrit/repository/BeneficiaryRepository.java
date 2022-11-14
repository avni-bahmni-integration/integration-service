package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.BeneficiaryConstant;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.dto.AmritFetchIdentityResponse;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.avni_integration_service.amrit.config.AmritMappingDbConstants.*;

@Component("BeneficiaryRepository")
public class BeneficiaryRepository extends AmritBaseRepository implements BeneficiaryConstant {
    private static final Logger logger = Logger.getLogger(BeneficiaryRepository.class);

    @Autowired
    public BeneficiaryRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository, @Qualifier("AmritRestTemplate") RestTemplate restTemplate, AmritApplicationConfig amritApplicationConfig, MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository, MappingGroupRepository mappingGroupRepository, MappingTypeRepository mappingTypeRepository) {
        super(integratingEntityStatusRepository, mappingGroupRepository, restTemplate, amritApplicationConfig, mappingMetaDataRepository, integrationSystemRepository, mappingTypeRepository, AmritEntityType.Beneficiary.name());
    }

    //// TODO: 08/11/22 correct implementation of methods
    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return new HashMap[0];
    }

    @Override
    public <T extends AmritBaseResponse> T createEvent(Subject subject, GeneralEncounter encounter, Class<T> returnType) {
        return createSingleEntity(amritApplicationConfig.getIdentityApiPrefix() +UPSERT_AMRIT_BENEFICIARY_RESOURCE_PATH, new HttpEntity<HashMap<String, Object>[]>(convertToBeneficiaryUpsertRequest(subject)), returnType);
    }

    private HashMap<String, Object>[] convertToBeneficiaryUpsertRequest(Subject subject) {
        HashMap<String, Object> beneficiary = new HashMap<String, Object>();
        populateObservations(beneficiary, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryRoot);

        HashMap<String, Object> demographics = new HashMap<String, Object>();
        populateObservations(demographics, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryDemographics);
        beneficiary.put(Beneficiary_Demographics_KeyName, demographics);

        HashMap<String, Object> phoneMaps = new HashMap<String, Object>();
        populateObservations(phoneMaps, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryPhoneMaps);
        beneficiary.put(Beneficiary_PhoneMaps_KeyName, phoneMaps);

        HashMap<String, Object> beneficiariesIdentity = new HashMap<String, Object>();
        beneficiariesIdentity.put(IDENTITY_TYPE, NATIONAL_ID);
        populateObservations(beneficiariesIdentity, subject, MappingGroup_Beneficiary, MappingType_BeneficiaryIdentity);
        beneficiary.put(Beneficiary_Identities_KeyName, new HashMap[]{beneficiariesIdentity});
        beneficiary.put(CREATED_BY, subject.getCreatedBy());

        return new HashMap[]{beneficiary};
    }


    public AmritFetchIdentityResponse getAmritId(String avniBeneficiaryUUID) {
        return getSingleEntityResponse(amritApplicationConfig.getIdentityApiPrefix() + FETCH_AMRIT_ID_RESOURCE_PATH, new HttpEntity<>(new String[] {avniBeneficiaryUUID}), AmritFetchIdentityResponse.class);
    }

}
