package org.avni_integration_service.integration_data.service;

import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IntegratingEntityStatusService {
    @Autowired
    private IntegratingEntityStatusRepository integratingEntityStatusRepository;

    public void saveEntityStatus(String entityType, Date lastModifiedDate) {
        IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(entityType);
        status.setReadUptoDateTime(lastModifiedDate);
        integratingEntityStatusRepository.save(status);
    }
}
