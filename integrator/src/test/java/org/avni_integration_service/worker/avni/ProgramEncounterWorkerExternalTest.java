package org.avni_integration_service.worker.avni;

import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniEnrolmentRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniProgramEncounterRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniSubjectRepository;
import org.avni_integration_service.service.MappingMetaDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Disabled
class ProgramEncounterWorkerExternalTest {
    @Autowired
    private ProgramEncounterWorker programEncounterWorker;
    @Autowired
    private EnrolmentWorker enrolmentWorker;
    @Autowired
    private SubjectWorker subjectWorker;
    @Autowired
    ConstantsRepository constantsRepository;
    @Autowired
    AvniProgramEncounterRepository programEncounterRepository;
    @Autowired
    MappingMetaDataService mappingMetaDataService;
    @Autowired
    AvniEnrolmentRepository avniEnrolmentRepository;
    @Autowired
    AvniSubjectRepository avniSubjectRepository;

    @Test
    public void testAllWorkers() {
        Constants constants = constantsRepository.findAllConstants();
        subjectWorker.cacheRunImmutables(constants);
        enrolmentWorker.cacheRunImmutables(constants);
        programEncounterWorker.cacheRunImmutables(constants);

        var subjects = List.of("3f908d5b-d336-4604-896a-e7481bfe5972", "9197245a-541f-4d1b-be47-a96f8843e727");

        for (var s : subjects) {
            var subject = avniSubjectRepository.getSubject(s);
            subjectWorker.processSubject(subject, true);
            var enrolments = (List<String>) subject.get("enrolments");
            for (var enl : enrolments) {
                var enrolment = avniEnrolmentRepository.getEnrolment(enl);
                enrolmentWorker.processEnrolment(enrolment, true);
                var encounters = (List<String>) enrolment.get("encounters");
                for (var encounterUuid : encounters) {
                    var programEncounter = programEncounterRepository.getProgramEncounter(encounterUuid);
                    programEncounterWorker.processProgramEncounter(programEncounter, true);
                }
            }
        }
    }

    @Test
    public void processProgramEncounter() {
        Constants constants = constantsRepository.findAllConstants();
        programEncounterWorker.cacheRunImmutables(constants);

        var programEncounter = programEncounterRepository.getProgramEncounter("c9add1fd-0be6-49db-a4a3-181e49f82a30");
        programEncounterWorker.processProgramEncounter(programEncounter, true);

    }
}
