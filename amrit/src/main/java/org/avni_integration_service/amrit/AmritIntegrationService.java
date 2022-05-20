package org.avni_integration_service.amrit;

import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmritIntegrationService {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final AvniSubjectRepository avniSubjectRepository;

    @Autowired
    public AmritIntegrationService(MappingMetaDataRepository mappingMetaDataRepository, AvniSubjectRepository avniSubjectRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.avniSubjectRepository = avniSubjectRepository;
    }
}
