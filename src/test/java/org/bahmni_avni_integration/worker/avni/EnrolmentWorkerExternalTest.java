package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.ConstantsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

@SpringBootTest
class EnrolmentWorkerExternalTest {
    @Autowired
    private EnrolmentWorker enrolmentWorker;
    @Autowired
    ConstantsRepository constantsRepository;

    @Test
    void processOneEnrolment() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<Enrolment> continueAfterOneRecord = enrolment -> false;
        enrolmentWorker.processEnrolments(constants, continueAfterOneRecord);
    }
}