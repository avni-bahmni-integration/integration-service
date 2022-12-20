package org.avni_integration_service.goonj;

import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.repository.DemandRepository;
import org.avni_integration_service.goonj.repository.DispatchRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoonjIntegrationService {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final DispatchRepository dispatchRepository;
    private final DemandRepository demandRepository;

    @Autowired
    public GoonjIntegrationService(MappingMetaDataRepository mappingMetaDataRepository, AvniSubjectRepository avniSubjectRepository, DispatchRepository dispatchRepository, DemandRepository demandRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.dispatchRepository = dispatchRepository;
        this.demandRepository = demandRepository;
    }
}
