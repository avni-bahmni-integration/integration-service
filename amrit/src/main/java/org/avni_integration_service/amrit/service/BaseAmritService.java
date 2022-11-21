package org.avni_integration_service.amrit.service;

import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.config.AmritMappingDbConstants;
import org.avni_integration_service.amrit.dto.AmritFetchIdentityResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.amrit.repository.HouseholdRepository;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseAmritService extends AmritMappingDbConstants {
    private static final Logger logger = LoggerFactory.getLogger(BaseAmritService.class);
    public static final String BENEFICIARY_REGISTRATION_NOT_COMPLETED_IN_AMRIT = "Beneficiary registration not completed in AMRIT";
    public static final String BENEFICIARY_NOT_FOUND_IN_AMRIT = "Beneficiary not found in AMRIT";
    public static final String REGEX = ":";
    public static final String NUMBERS_ONLY_REGEX = "\\d+";
    protected final AvniAmritErrorService avniAmritErrorService;
    protected final BeneficiaryRepository beneficiaryRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystemRepository integrationSystemRepository;

    protected BaseAmritService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository,
                               IntegrationSystemRepository integrationSystemRepository,
                               MappingMetaDataRepository mappingMetaDataRepository) {
        this.avniAmritErrorService = avniAmritErrorService;
        this.beneficiaryRepository = beneficiaryRepository;
        this.integrationSystemRepository = integrationSystemRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }


    public boolean wasFetchOfAmritIdSuccessful(Subject beneficiary, boolean throwExceptionIfNotFound) {
        AmritFetchIdentityResponse response = beneficiaryRepository.getAmritId(beneficiary.getUuid());
        try {
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                String externalId =  response.getIds().get(0);
                String[] entry = externalId.split(REGEX);
                if (entry.length == 2) {
                    externalId = entry[0].trim();
                }
                if (!externalId.equals(BENEFICIARY_REGISTRATION_NOT_COMPLETED_IN_AMRIT)
                        && !externalId.equals(BENEFICIARY_NOT_FOUND_IN_AMRIT)
                        && externalId.matches(NUMBERS_ONLY_REGEX)) {
                    beneficiary.setExternalId(externalId);
                    return true;
                } else if (externalId.equals(BENEFICIARY_NOT_FOUND_IN_AMRIT)) {
                    if (throwExceptionIfNotFound) {
                        throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Beneficiary not found " + beneficiary.getUuid());
                    } else {
                        return true;
                    }
                } else if (externalId.equals(BENEFICIARY_REGISTRATION_NOT_COMPLETED_IN_AMRIT)) {
                    return false;
                } else {
                    throw new HttpServerErrorException(HttpStatus.EXPECTATION_FAILED,
                            "Invalid response for getAmritId request " + response.getData());
                }
            } else {
                throw new HttpServerErrorException(HttpStatus.resolve((int) response.getStatusCode()),
                        "Failed to obtain successful response from Amrit " + response.getErrorMessage());
            }
        } catch(Exception e) {
            avniAmritErrorService.errorOccurred(beneficiary.getUuid(),
                    AmritErrorType.BeneficiaryAmritIDFetchError,
                    AmritEntityType.Beneficiary, e.getLocalizedMessage());
        }
        return false;
    }
}
