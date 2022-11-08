package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.repository.BeneficiaryRepository;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService {
    private static final Logger logger = Logger.getLogger(BeneficiaryService.class);
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final AmritApplicationConfig amritApplicationConfig;
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                              AmritApplicationConfig amritApplicationConfig, BeneficiaryRepository beneficiaryRepository) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.amritApplicationConfig = amritApplicationConfig;
        this.beneficiaryRepository = beneficiaryRepository;
    }
}
