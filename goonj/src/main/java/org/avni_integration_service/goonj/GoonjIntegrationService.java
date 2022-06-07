package org.avni_integration_service.goonj;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.repository.DemandRepository;
import org.avni_integration_service.goonj.repository.DispatchRepository;
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
    private final DemandRepository demandRepository;
    private final DispatchRepository dispatchRepository;

    @Autowired
    public GoonjIntegrationService(MappingMetaDataRepository mappingMetaDataRepository, AvniSubjectRepository avniSubjectRepository,
                                   DemandRepository demandRepository, DispatchRepository dispatchRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.demandRepository = demandRepository;
        this.dispatchRepository = dispatchRepository;
    }

    public void pushDemandToAvni( LocalDateTime localDateTime) {
        HashMap<String, Object>[] demands = demandRepository.getDemands( localDateTime);
        for (Map<String, Object> demand : demands) {
            Subject subject = Demand.from(demand);
            avniSubjectRepository.create(subject);
        }
    }

    public void pushDispatchToAvni() {
    }
}
