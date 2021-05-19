package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

@SpringBootTest
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
        Enrolment enrolment = avniEnrolmentRepository.getEnrolment("83bede87-68db-2edc-771b-d214f64d08f0");
        enrolmentWorker.processEnrolment(enrolment);
    }
}