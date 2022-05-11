package org.avni_integration_service.worker.avni;

import org.avni_integration_service.contract.avni.Enrolment;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniEnrolmentRepository;
import org.avni_integration_service.service.MappingMetaDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class EnrolmentWorkerExternalTest {
    @Autowired
    EnrolmentWorker enrolmentWorker;

    @Autowired
    ConstantsRepository constantsRepository;

    @Autowired
    MappingMetaDataService mappingMetaDataService;

    @Autowired
    AvniEnrolmentRepository avniEnrolmentRepository;

    //Useful when testing things like update
    @Test
    void processSpecificEnrolment() {
        Constants constants = constantsRepository.findAllConstants();
        enrolmentWorker.cacheRunImmutables(constants);
        Enrolment enrolment = avniEnrolmentRepository.getEnrolment("4f48966e-06ec-4d62-9822-068699b55942");
        enrolmentWorker.processEnrolment(enrolment, true);
        enrolment = avniEnrolmentRepository.getEnrolment("c9c18e86-df00-4e84-a867-869a7134bb76");
        enrolmentWorker.processEnrolment(enrolment, true);
        enrolment = avniEnrolmentRepository.getEnrolment("0f61c57c-0535-4635-a513-c168e6007a72");
        enrolmentWorker.processEnrolment(enrolment, true);
    }
}
