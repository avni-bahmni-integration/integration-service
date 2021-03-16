package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniProgramEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
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
        avniProgramEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public ProgramEncounter getProgramEncounter(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), bahmniSplitEncounter.getOpenMRSEncounterUuid());
        // OpenMRS encounter uuid will be shared by multiple entities in Avni, hence encounter type is required
        return avniProgramEncounterRepository.get(metaData.getAvniMappedName(bahmniSplitEncounter.getFormConceptSetUuid()), obsCriteria);
    }

    public void create(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, Enrolment enrolment) {
        ProgramEncounter encounter = openMRSEncounterMapper.mapToAvniProgramEncounter(splitEncounter, metaData, enrolment);
        avniProgramEncounterRepository.create(encounter);
    }
}