package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.ConstantsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

@SpringBootTest
class SubjectWorkerExternalTest {
    @Autowired
    private SubjectWorker subjectWorker;
    @Autowired
    ConstantsRepository constantsRepository;

    @Test
    void processOneSubject() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<Subject> processOnlyOneRecord = subject -> false;
        subjectWorker.processSubjects(constants, processOnlyOneRecord);
    }

    @Test
    @Disabled("Useful when giving full run")
    public void processSubjects() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<Subject> processEverything = subject -> true;
        subjectWorker.processSubjects(constants, processEverything);
    }
}