package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.AvniBaseContract;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.domain.AvniEntityStatus;
import org.bahmni_avni_integration.domain.AvniEntityType;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
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

    private void saveEntityStatus(AvniEntityType avniEntityType, AvniBaseContract avniBaseContract) {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(avniEntityType);
        status.setReadUpto(avniBaseContract.getLastModifiedDate());
        avniEntityStatusRepository.save(status);
    }
}