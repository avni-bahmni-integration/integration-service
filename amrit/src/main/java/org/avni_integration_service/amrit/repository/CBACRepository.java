package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.BeneficiaryConstants;
import org.avni_integration_service.amrit.config.CBACConstants;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.util.DateTimeUtil;
import org.avni_integration_service.avni.domain.AvniBaseContract;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;

import static org.avni_integration_service.amrit.config.AmritMappingDbConstants.*;

@Component("CBACRepository")
public class CBACRepository extends AmritBaseRepository implements CBACConstants, BeneficiaryConstants {
    private static final Logger logger = Logger.getLogger(CBACRepository.class);

    @Autowired
    public CBACRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
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
    public <T extends AmritBaseResponse> T createEvent(AvniBaseContract subject, Enrolment encounter, Class<T> returnType) {
        return createSingleEntity(amritApplicationConfig.getIdentityApiPrefix() +UPSERT_AMRIT_CBAC_RESOURCE_PATH,
                new HttpEntity<HashMap<String, Object>[]>(convertToCBACUpsertRequest((Subject) subject, encounter)), returnType);
    }

    private HashMap<String, Object>[] convertToCBACUpsertRequest(Subject subject, Enrolment encounter) {
        HashMap<String, Object> cBACObs = new HashMap<String, Object>();
        populateObservations(cBACObs, encounter, MappingGroup_CBAC, MappingType_CBACRoot,
                MappingType_CBACObservations);
        initMiscFields(subject, encounter, cBACObs);
        logger.debug(String.format("Converting encounter to cBACObs [%s] and [%s]", encounter, cBACObs));
        return new HashMap[]{cBACObs};
    }

    private void initMiscFields(Subject subject, Enrolment enrolment, HashMap<String, Object> cBACObs) {
        if(StringUtils.hasText(subject.getExternalId())) {
            cBACObs.put(BENEFICIARY_REG_ID, subject.getExternalId());
        }
        cBACObs.put(AVNI_BENEFICIARY_ID, subject.getUuid());
        cBACObs.put(AVNI_CBAC_ID, enrolment.getUuid());
        cBACObs.put(VAN_ID, VAN_ID_VALUE);
        cBACObs.put(LAST_MODIFIED_BY, enrolment.getLastModifiedBy());
        cBACObs.put(LAST_MODIFIED_DATE, DateTimeUtil.formatDateTime(enrolment.getLastModifiedDate()));
        cBACObs.put(SYNCED_BY, enrolment.getCreatedBy());
        cBACObs.put(SYNCED_DATE, DateTimeUtil.formatDateTime(new Date()));
        cBACObs.put(CREATED_BY, enrolment.getCreatedBy());
        cBACObs.put(CREATED_DATE, DateTimeUtil.formatDateTime(enrolment.getCreateDate()));
        cBACObs.put(DELETED, enrolment.getVoided());
    }

}
