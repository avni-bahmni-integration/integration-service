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

    @Test
    void processAllEnrolments() {
        Constants constants = constantsRepository.findAllConstants();
        enrolmentWorker.cacheRunImmutables(constants);
        enrolmentWorker.processEnrolment();
    }

    //Useful when testing things like update
    @Test
    void processSpecificEnrolment() {
        Constants constants = constantsRepository.findAllConstants();
        enrolmentWorker.cacheRunImmutables(constants);
        Enrolment enrolment = avniEnrolmentRepository.getEnrolment("ef2aa636-4e8b-4c15-97e6-8e028cbbe4b7");
        enrolmentWorker.processEnrolment(enrolment);
    }
}