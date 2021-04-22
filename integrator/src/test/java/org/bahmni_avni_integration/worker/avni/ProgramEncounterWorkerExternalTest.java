package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniProgramEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

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

    //Useful when testing things like update
    @Test
    public void processSpecificEncounter() {
        Constants constants = constantsRepository.findAllConstants();
        Predicate<ProgramEncounter> continueAfterOneRecord = subject -> false;
        var subjectToPatientMetaData = mappingMetaDataService.getForSubjectToPatient();
        var programEncounter = programEncounterRepository.getProgramEncounter("93938ad7-710d-4b10-8a74-431c11a6ac94");
        programEncounterWorker.processProgramEncounter(constants, continueAfterOneRecord, programEncounter, subjectToPatientMetaData);
    }
}