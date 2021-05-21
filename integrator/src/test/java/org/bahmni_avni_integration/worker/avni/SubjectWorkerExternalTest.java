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
        subjectWorker.processSubject(avniSubjectRepository.getSubject("85645dd8-7b18-4531-241f-0e1cea091ddc"));
        subjectWorker.processSubject(avniSubjectRepository.getSubject("5c67e7fc-1347-4436-ae58-d2425c70c106"));
    }
}