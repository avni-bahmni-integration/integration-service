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
import org.springframework.web.client.HttpStatusCodeException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService extends BaseAmritService {
    private static final Logger logger = Logger.getLogger(BeneficiaryService.class);
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
        try {
            AmritFetchIdentityResponse response = beneficiaryRepository.getAmritId(beneficiary.getUuid());
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                Map<String, String> idToUUIDMap = response.getIds().stream().map(e-> {
                    String[] entry = e.split(":");
                    return Map.entry(entry[1], entry[0]);
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
                if(!idToUUIDMap.get(beneficiary.getUuid()).equals("Beneficiary registration not completed in AMRIT") //TODO what to do in this case.? should we return true or false
                        && !idToUUIDMap.get(beneficiary.getUuid()).equals("Beneficiary not found in AMRIT")) {
                    return true;
                } else if ( !idToUUIDMap.get(beneficiary.getUuid()).equals("Beneficiary not found in AMRIT")
                        && throwExceptionIfNotFound) {
                    //TODO check if there api returns 404 for not found beneficiary id
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Beneficiary not found " + beneficiary.getUuid());
                }
            } else {
                avniAmritErrorService.errorOccurred(beneficiary.getUuid(),
                        AmritErrorType.BeneficiaryAmritIDFetchError,
                        AmritEntityType.Beneficiary, response.getErrorMessage());
            }
        } catch (Exception e) {
            //TODO Amrit threw an error, if its a network issue, throw exceptions
            //TODO If its a business error, like 404, 400, then ignore the exception and return false
            throw e;
        }
        return false;
    }

}
