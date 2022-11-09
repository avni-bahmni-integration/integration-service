package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.dto.AmritFetchIdentityResponse;
import org.avni_integration_service.amrit.dto.AmritUpsertBeneficiaryResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.avni.domain.Subject;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService {
    private static final Logger logger = Logger.getLogger(BeneficiaryService.class);
    private final AvniAmritErrorService avniAmritErrorService;
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository) {
        this.avniAmritErrorService = avniAmritErrorService;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public void createOrUpdateBeneficiary(Subject beneficiary) {
        if(wasFetchOfAmritIdSuccessful(beneficiary)) {
            beneficiaryRepository.createEvent(beneficiary, null, AmritUpsertBeneficiaryResponse.class);
        }
    }

    public boolean wasFetchOfAmritIdSuccessful(Subject beneficiary) {
        try {
            AmritFetchIdentityResponse response = beneficiaryRepository.getAmritId(beneficiary.getUuid());
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return true;
            } else {
                avniAmritErrorService.errorOccurred(beneficiary.getUuid(),
                        AmritErrorType.BeneficiaryAmritIDFetchError,
                        AmritEntityType.BENEFICIARY, response.getErrorMessage());
            }
        } catch (Exception e) {
            //TODO Amrit threw an error, if its a network issue, throw exceptions
            //TODO If its a business error, like 404, 400, then ignore the exception and return false
            throw e;
        }
        return false;
    }

}
