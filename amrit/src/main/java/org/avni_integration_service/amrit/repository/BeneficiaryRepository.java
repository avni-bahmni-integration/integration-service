package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritMappingDbConstants;
import org.avni_integration_service.amrit.config.BeneficiaryConstant;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Component("BeneficiaryRepository")
public class BeneficiaryRepository extends AmritBaseRepository implements BeneficiaryConstant {
    private static final Logger logger = Logger.getLogger(BeneficiaryRepository.class);
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystem integrationSystem;

    @Autowired
    public BeneficiaryRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                 @Qualifier("AmritRestTemplate") RestTemplate restTemplate, AmritApplicationConfig amritApplicationConfig,
                                 MappingMetaDataRepository mappingMetaDataRepository,
                                 IntegrationSystemRepository integrationSystemRepository, AvniHttpClient avniHttpClient) {
        super(integratingEntityStatusRepository, restTemplate,
                amritApplicationConfig, AmritEntityType.BENEFICIARY.name(), avniHttpClient);
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystem = integrationSystemRepository.findByName(AmritMappingDbConstants.IntSystemName);
    }

    //// TODO: 08/11/22 correct implementation of methods
    @Override
    public HashMap<String, Object>[] fetchEvents() {
        return new HashMap[0];
    }

    @Override
    public List<String> fetchDeletionEvents() {
        return null;
    }

    @Override
    public HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter) {
        return new HashMap[0];
    }
}
