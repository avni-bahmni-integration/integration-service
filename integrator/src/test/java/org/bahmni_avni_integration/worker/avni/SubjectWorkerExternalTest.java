package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.MappingMetaDataService;
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
    @Autowired
    AvniSubjectRepository avniSubjectRepository;
    @Autowired
    MappingMetaDataService mappingMetaDataService;

    @Test
    void processOneSubject() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<Subject> continueAfterOneRecord = subject -> false;
        subjectWorker.processSubjects(constants, continueAfterOneRecord);
    }

    @Test
    @Disabled("Useful when giving full run")
    public void processSubjects() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<Subject> continueAfterOneRecord = subject -> true;
        subjectWorker.processSubjects(constants, continueAfterOneRecord);
    }

    //Useful when testing things like update
    @Test
    public void processSpecificSubject() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<Subject> continueAfterOneRecord = subject -> false;
        var subjectToPatientMetaData = mappingMetaDataService.getForSubjectToPatient();
        var subject = avniSubjectRepository.getSubject("8cd1330b-02f2-45e0-818a-fa8a1e238867");
        subjectWorker.processSubject(constants, continueAfterOneRecord, subjectToPatientMetaData, subject);
    }
}