package org.avni_integration_service.worker.avni;

import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniSubjectRepository;
import org.avni_integration_service.service.MappingMetaDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
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
        subjectWorker.cacheRunImmutables(constants);
        subjectWorker.processSubjects();
    }

    @Test
    @Disabled("Useful when giving full run")
    public void processSubjects() {
        Constants constants = constantsRepository.findAllConstants();
        subjectWorker.cacheRunImmutables(constants);
        subjectWorker.processSubjects();
    }

    //Useful when testing things like update
    @Test
    public void processSpecificSubject() {
        Constants constants = constantsRepository.findAllConstants();
        subjectWorker.cacheRunImmutables(constants);
        // Demo non existing patient
//        subjectWorker.processSubject(avniSubjectRepository.getSubject("3f908d5b-d336-4604-896a-e7481bfe5972"));
        // Demo existing patient
//        subjectWorker.processSubject(avniSubjectRepository.getSubject("9197245a-541f-4d1b-be47-a96f8843e727"));

        // Test date greater than current issue
//        subjectWorker.processSubject(avniSubjectRepository.getSubject("372233b3-a381-9333-34ad-ca34f86f6b17"));

        //Test identifier null issue
        subjectWorker.processSubject(avniSubjectRepository.getSubject("fa6ba4bf-2772-0545-2a9f-09bcb0828174"), true);
    }
}
