package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniProgramEncounterRepository;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProgramEncounterWorkerExternalTest {
    @Autowired
    private ProgramEncounterWorker programEncounterWorker;
    @Autowired
    ConstantsRepository constantsRepository;
    @Autowired
    AvniProgramEncounterRepository programEncounterRepository;
    @Autowired
    MappingMetaDataService mappingMetaDataService;
    @Autowired
    AvniEnrolmentRepository avniEnrolmentRepository;

    //Useful when testing things like update
    @Test
    public void processProgramEncountersOfAnEnrolment() {
        Constants constants = constantsRepository.findAllConstants();
        programEncounterWorker.cacheRunImmutables(constants);
        var enrolment = avniEnrolmentRepository.getEnrolment("cec8b907-31d2-4eba-b725-b1b607c10c9e");
        var encounters = (List<String>) enrolment.get("encounters");
        for (String encounterUuid : encounters) {
            var programEncounter = programEncounterRepository.getProgramEncounter(encounterUuid);
            programEncounterWorker.processProgramEncounter(programEncounter);
        }
    }
}