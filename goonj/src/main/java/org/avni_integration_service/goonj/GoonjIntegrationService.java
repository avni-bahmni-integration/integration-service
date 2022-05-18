package org.avni_integration_service.goonj;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.avni_integration_service.goonj.domain.Dispatch;
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
    private final DispatchRepository dispatchRepository;

    @Autowired
    public GoonjIntegrationService(MappingMetaDataRepository mappingMetaDataRepository, AvniSubjectRepository avniSubjectRepository, DispatchRepository dispatchRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.dispatchRepository = dispatchRepository;
    }

    public void pushDemandToAvni(AuthResponse authResponse, LocalDateTime localDateTime) {
        HashMap<String, Object>[] dispatches = dispatchRepository.getDispatches(authResponse, localDateTime);
        for (Map<String, Object> dispatch : dispatches) {
            Subject subject = Dispatch.from(dispatch);
            avniSubjectRepository.create(subject);
        }
    }

    public void pushDispatchToAvni() {
    }
}
