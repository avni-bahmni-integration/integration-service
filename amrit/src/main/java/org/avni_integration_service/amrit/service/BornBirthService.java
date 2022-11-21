package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.amrit.repository.BornBirthRepository;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Service;

@Service
public class BornBirthService extends BaseAmritService {
    private static final Logger logger = Logger.getLogger(BornBirthService.class);
    protected final BornBirthRepository bornBirthRepository;

    public BornBirthService(AvniAmritErrorService avniAmritErrorService, BeneficiaryRepository beneficiaryRepository,
                            IntegrationSystemRepository integrationSystemRepository,
                            MappingMetaDataRepository mappingMetaDataRepository, BornBirthRepository bornBirthRepository) {
        super(avniAmritErrorService, beneficiaryRepository, integrationSystemRepository, mappingMetaDataRepository);
        this.bornBirthRepository = bornBirthRepository;
    }

    public void createOrUpdateBornBirth(Subject subject, GeneralEncounter encounter) {
        if (wasFetchOfAmritIdSuccessful(subject, false)) {
            bornBirthRepository.createEvent(subject, encounter, AmritBaseResponse.class);
        }
    }
}
