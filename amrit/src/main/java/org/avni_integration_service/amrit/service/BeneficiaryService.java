package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.dto.AmritFetchIdentityResponse;
import org.avni_integration_service.amrit.dto.AmritUpsertBeneficiaryResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService extends BaseAmritService {
    private static final Logger logger = Logger.getLogger(BeneficiaryService.class);
    public static final String BENEFICIARY_REGISTRATION_NOT_COMPLETED_IN_AMRIT = "Beneficiary registration not completed in AMRIT";
    public static final String BENEFICIARY_NOT_FOUND_IN_AMRIT = "Beneficiary not found in AMRIT";
    public static final String REGEX = ":";
    private final AvniAmritErrorService avniAmritErrorService;
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository,
                              MappingMetaDataRepository mappingMetaDataRepository,
                              IntegrationSystemRepository integrationSystemRepository) {
        super(mappingMetaDataRepository, integrationSystemRepository);
        this.avniAmritErrorService = avniAmritErrorService;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public void createOrUpdateBeneficiary(Subject beneficiary) {
        if (wasFetchOfAmritIdSuccessful(beneficiary, false)) {
            beneficiaryRepository.createEvent(beneficiary, null, AmritUpsertBeneficiaryResponse.class);
        }
    }

    public boolean wasFetchOfAmritIdSuccessful(Subject beneficiary, boolean throwExceptionIfNotFound) {
        AmritFetchIdentityResponse response = beneficiaryRepository.getAmritId(beneficiary.getUuid());
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            Map<String, String> idToUUIDMap = response.getIds().stream().map(e-> {
                String[] entry = e.split(REGEX);
                if(entry.length != 2) {
                    throw new RuntimeException("Issue converting response to idToUUIDMap " + e);
                }
                return Map.entry(entry[1].trim(), entry[0].trim());
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            if(!idToUUIDMap.get(beneficiary.getUuid()).equals(BENEFICIARY_REGISTRATION_NOT_COMPLETED_IN_AMRIT)
                    && !idToUUIDMap.get(beneficiary.getUuid()).equals(BENEFICIARY_NOT_FOUND_IN_AMRIT)) {
                beneficiary.setExternalId(idToUUIDMap.get(beneficiary.getUuid()));
                return true;
            } else if ( idToUUIDMap.get(beneficiary.getUuid()).equals(BENEFICIARY_NOT_FOUND_IN_AMRIT)) {
                if(throwExceptionIfNotFound) {
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Beneficiary not found " + beneficiary.getUuid());
                } else {
                    return true;
                }
            } else if ( idToUUIDMap.get(beneficiary.getUuid()).equals(BENEFICIARY_REGISTRATION_NOT_COMPLETED_IN_AMRIT)) {
                //TODO Uncomment this post Amrit fixing the error
//                throw new HttpServerErrorException(HttpStatus.EXPECTATION_FAILED, "Beneficiary registration not completed " + beneficiary.getUuid());
            }
        } else {
            avniAmritErrorService.errorOccurred(beneficiary.getUuid(),
                    AmritErrorType.BeneficiaryAmritIDFetchError,
                    AmritEntityType.Beneficiary, response.getErrorMessage());
        }
        return false;
    }

}
