package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.dto.AmritUpsertBeneficiaryResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService extends BaseAmritService {
    private static final Logger logger = Logger.getLogger(BeneficiaryService.class);

    public BeneficiaryService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository,
                              IntegrationSystemRepository integrationSystemRepository,
        MappingMetaDataRepository mappingMetaDataRepository) {
        super(avniAmritErrorService, beneficiaryRepository, integrationSystemRepository, mappingMetaDataRepository);
    }

    public void createOrUpdateBeneficiary(Subject beneficiary) {
        if (wasFetchOfAmritIdSuccessful(beneficiary, false, true)) {
            beneficiaryRepository.createEvent(beneficiary, null, AmritUpsertBeneficiaryResponse.class);
        }
    }
}
