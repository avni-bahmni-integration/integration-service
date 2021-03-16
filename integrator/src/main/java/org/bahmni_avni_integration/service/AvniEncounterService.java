package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AvniEncounterService {
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;
    @Autowired
    private AvniEncounterRepository avniEncounterRepository;

    public void update(BahmniSplitEncounter bahmniSplitEncounter, GeneralEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(bahmniSplitEncounter, bahmniEncounterToAvniEncounterMetaData, avniPatient);
        avniEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public GeneralEncounter getGeneralEncounter(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), bahmniSplitEncounter.getOpenMRSEncounterUuid());
        // OpenMRS encounter uuid will be shared by multiple entities in Avni, hence encounter type is required
        return avniEncounterRepository.getEncounter(metaData.getAvniMappedName(bahmniSplitEncounter.getFormConceptSetUuid()), obsCriteria);
    }

    public void create(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(splitEncounter, metaData, avniPatient);
        avniEncounterRepository.create(encounter);
    }
}