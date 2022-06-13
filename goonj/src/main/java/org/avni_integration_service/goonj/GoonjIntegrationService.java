package org.avni_integration_service.goonj;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.repository.DemandRepositoryGoonj;
import org.avni_integration_service.goonj.repository.DispatchRepositoryGoonj;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class GoonjIntegrationService {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final DispatchRepositoryGoonj dispatchRepository;
    private final DemandRepositoryGoonj demandRepository;

    @Autowired
    public GoonjIntegrationService(MappingMetaDataRepository mappingMetaDataRepository, AvniSubjectRepository avniSubjectRepository, DispatchRepositoryGoonj dispatchRepository, DemandRepositoryGoonj demandRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.dispatchRepository = dispatchRepository;
        this.demandRepository = demandRepository;
    }
}
