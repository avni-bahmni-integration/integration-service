package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.dto.AmritUpsertBeneficiaryResponse;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.amrit.repository.BornBirthRepository;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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

    public void createOrUpdateBornBirth(Subject subject, Enrolment enrolment) {
        try {
            if (wasFetchOfAmritIdSuccessful(subject, true, true)) {
                bornBirthRepository.createEvent(subject, enrolment, AmritBaseResponse.class);
            }
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                beneficiaryRepository.createEvent(subject, null, AmritUpsertBeneficiaryResponse.class);
            }
            throw e;
        }
    }
}
