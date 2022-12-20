package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.avni.domain.*;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvniEntityStatusService {
    @Autowired
    private IntegratingEntityStatusRepository integratingEntityStatusRepository;

    public void saveEntityStatus(Subject subject) {
        saveEntityStatus(AvniEntityType.Subject, subject);
    }

    public void saveEntityStatus(Enrolment enrolment) {
        saveEntityStatus(AvniEntityType.Enrolment, enrolment);
    }

    public void saveEntityStatus(ProgramEncounter programEncounter) {
        saveEntityStatus(AvniEntityType.ProgramEncounter, programEncounter);
    }

    public void saveEntityStatus(GeneralEncounter generalEncounter) {
        saveEntityStatus(AvniEntityType.GeneralEncounter, generalEncounter);
    }

    private void saveEntityStatus(AvniEntityType avniEntityType, AvniBaseContract avniBaseContract) {
        IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(avniEntityType.name());
        status.setReadUptoDateTime(avniBaseContract.getLastModifiedDate());
        integratingEntityStatusRepository.save(status);
    }
}
