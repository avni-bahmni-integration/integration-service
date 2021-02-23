package org.bahmni_avni_integration.service;

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
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
        status.setReadUpto(subject.getLastModifiedDate());
        avniEntityStatusRepository.save(status);
    }

    public void saveEntityStatus(Enrolment enrolment) {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Enrolment);
        status.setReadUpto(enrolment.getLastModifiedDate());
        avniEntityStatusRepository.save(status);
    }
}