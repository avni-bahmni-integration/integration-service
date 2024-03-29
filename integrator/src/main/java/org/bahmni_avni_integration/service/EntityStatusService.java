package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.*;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityStatusService {
    @Autowired
    private AvniEntityStatusRepository avniEntityStatusRepository;

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
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(avniEntityType);
        status.setReadUpto(avniBaseContract.getLastModifiedDate());
        avniEntityStatusRepository.save(status);
    }
}
