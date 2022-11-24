package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.amrit.repository.HouseholdRepository;
import org.avni_integration_service.avni.domain.Household;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Service;

@Service
public class HouseholdService extends BaseAmritService {
    private static final Logger logger = Logger.getLogger(HouseholdService.class);
    protected final HouseholdRepository householdRepository;

    public HouseholdService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository,
                            IntegrationSystemRepository integrationSystemRepository,
                            MappingMetaDataRepository mappingMetaDataRepository, HouseholdRepository householdRepository) {
        super(avniAmritErrorService, beneficiaryRepository, integrationSystemRepository, mappingMetaDataRepository);
        this.householdRepository = householdRepository;
    }

    public void createOrUpdateHousehold(Household household) {
        if (wasFetchOfAmritIdSuccessful(household.getMemberSubject(), true, true)) {
            householdRepository.createEvent(household, AmritBaseResponse.class);
        }
    }
}
