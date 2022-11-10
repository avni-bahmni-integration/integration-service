package org.avni_integration_service.amrit.service;

import org.avni_integration_service.amrit.config.AmritMappingDbConstants;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAmritService extends AmritMappingDbConstants {
    private static final Logger logger = LoggerFactory.getLogger(BaseAmritService.class);
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystemRepository integrationSystemRepository;

    protected BaseAmritService(MappingMetaDataRepository mappingMetaDataRepository,
                               IntegrationSystemRepository integrationSystemRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystemRepository = integrationSystemRepository;
    }
}
