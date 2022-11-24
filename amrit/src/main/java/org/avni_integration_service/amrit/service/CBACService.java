package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.amrit.repository.CBACRepository;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Service;

@Service
public class CBACService extends BaseAmritService {
    private static final Logger logger = Logger.getLogger(CBACService.class);
    protected final CBACRepository cBACRepository;

    public CBACService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository,
                       IntegrationSystemRepository integrationSystemRepository,
                       MappingMetaDataRepository mappingMetaDataRepository, CBACRepository cBACRepository) {
        super(avniAmritErrorService, beneficiaryRepository, integrationSystemRepository, mappingMetaDataRepository);
        this.cBACRepository = cBACRepository;
    }

    public void createOrUpdateCBAC(Subject subject, GeneralEncounter encounter) {
        if (wasFetchOfAmritIdSuccessful(subject, true, true)) {
            cBACRepository.createEvent(subject, encounter, AmritBaseResponse.class);
        }
    }
}
