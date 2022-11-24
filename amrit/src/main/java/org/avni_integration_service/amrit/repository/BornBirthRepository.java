package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.BeneficiaryConstants;
import org.avni_integration_service.amrit.config.BornBirthConstants;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.service.AmritTokenService;
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

@Component("BornBirthRepository")
public class BornBirthRepository extends AmritBaseRepository implements BornBirthConstants, BeneficiaryConstants {
    private static final Logger logger = Logger.getLogger(BornBirthRepository.class);

    @Autowired
    public BornBirthRepository(AmritTokenService amritTokenService, IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               @Qualifier("AmritRestTemplate") RestTemplate restTemplate,
                               AmritApplicationConfig amritApplicationConfig,
                               MappingMetaDataRepository mappingMetaDataRepository,
                               IntegrationSystemRepository integrationSystemRepository,
                               MappingGroupRepository mappingGroupRepository,
                               MappingTypeRepository mappingTypeRepository) {
        super(amritTokenService, integratingEntityStatusRepository, mappingGroupRepository, restTemplate, amritApplicationConfig,
                mappingMetaDataRepository, integrationSystemRepository, mappingTypeRepository, AmritEntityType.Beneficiary.name());
    }

    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return new HashMap[0];
    }

    @Override
    public <T extends AmritBaseResponse> T createEvent(AvniBaseContract subject, AvniBaseContract enrolment, Class<T> returnType) {
        return createSingleEntity(amritApplicationConfig.getIdentityApiPrefix() +UPSERT_AMRIT_BORN_BIRTH_RESOURCE_PATH,
                new HttpEntity<HashMap<String, Object>>(convertToBornBirthUpsertRequest((Subject) subject, (Enrolment) enrolment)), returnType);
    }

    private HashMap<String, Object> convertToBornBirthUpsertRequest(Subject subject, Enrolment enrolment) {
        HashMap<String, Object> bornBirthObs = new HashMap<String, Object>();
        populateObservations(bornBirthObs, enrolment, MappingGroup_BornBirth, MappingType_BornBirthRoot,
                MappingType_BornBirthObservations);
        initMiscFields(subject, enrolment, bornBirthObs);
        logger.debug(String.format("Converting enrolment to bornBirthObs [%s] and [%s]", enrolment, bornBirthObs));
        return bornBirthObs;
    }

    private void initMiscFields(Subject subject, Enrolment encounter, HashMap<String, Object> bornBirthObs) {
        if(StringUtils.hasText(subject.getExternalId())) {
            bornBirthObs.put(BENEFICIARY_REG_ID, subject.getExternalId());
        }
        bornBirthObs.put(AVNI_BENEFICIARY_ID, subject.getUuid());
        bornBirthObs.put(AVNI_BORN_BIRTH_ID, encounter.getUuid());
        bornBirthObs.put(VAN_ID, VAN_ID_VALUE);
        bornBirthObs.put(LAST_MODIFIED_BY, encounter.getLastModifiedBy());
        bornBirthObs.put(LAST_MODIFIED_DATE, DateTimeUtil.formatDateTime(encounter.getLastModifiedDate()));
        bornBirthObs.put(SYNCED_BY, encounter.getCreatedBy());
        bornBirthObs.put(SYNCED_DATE, DateTimeUtil.formatDateTime(new Date()));
        bornBirthObs.put(CREATED_BY, encounter.getCreatedBy());
        bornBirthObs.put(CREATED_DATE, DateTimeUtil.formatDateTime(encounter.getCreateDate()));
        bornBirthObs.put(DELETED, encounter.getVoided());
    }

}
