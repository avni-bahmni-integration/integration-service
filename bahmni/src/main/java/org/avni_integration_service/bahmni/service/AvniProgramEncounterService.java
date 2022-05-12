package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.mapper.OpenMRSEncounterMapper;
import org.avni_integration_service.contract.avni.Enrolment;
import org.avni_integration_service.contract.avni.ProgramEncounter;
import org.avni_integration_service.contract.repository.AvniProgramEncounterRepository;
import org.avni_integration_service.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.avni_integration_service.bahmni.repository.BahmniSplitEncounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AvniProgramEncounterService {
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;
    @Autowired
    private AvniProgramEncounterRepository avniProgramEncounterRepository;

    public void update(BahmniSplitEncounter bahmniSplitEncounter, ProgramEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, Enrolment enrolment) {
        ProgramEncounter encounter = openMRSEncounterMapper.mapToAvniProgramEncounter(bahmniSplitEncounter, bahmniEncounterToAvniEncounterMetaData, enrolment);
        encounter.setVoided(bahmniSplitEncounter.isVoided());
        avniProgramEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public ProgramEncounter getProgramEncounter(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), bahmniSplitEncounter.getOpenMRSEncounterUuid());
        // OpenMRS encounter uuid will be shared by multiple entities in Avni, hence encounter type is required
        return avniProgramEncounterRepository.get(metaData.getAvniMappedName(bahmniSplitEncounter.getFormConceptSetUuid()), obsCriteria);
    }

    public void create(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, Enrolment enrolment) {
        if (splitEncounter.isVoided()) return;

        ProgramEncounter encounter = openMRSEncounterMapper.mapToAvniProgramEncounter(splitEncounter, metaData, enrolment);
        avniProgramEncounterRepository.create(encounter);
    }
}
