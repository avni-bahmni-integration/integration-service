package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.domain.ErrorType;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
import org.bahmni_avni_integration.repository.avni.AvniEncounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class AvniEncounterService {
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;
    @Autowired
    private AvniEncounterRepository avniEncounterRepository;
    @Autowired
    private ErrorService errorService;

    public void update(OpenMRSFullEncounter openMRSEncounter, GeneralEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(openMRSEncounter, bahmniEncounterToAvniEncounterMetaData, avniPatient);
        avniEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public GeneralEncounter getGeneralEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put(metaData.getBahmniEntityUuidConcept(), openMRSEncounter.getUuid());
        return avniEncounterRepository.getEncounter(encounterCriteria);
    }

    public void processSubjectIdChanged(GeneralEncounter existingEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        errorService.errorOccurred(existingEncounter, ErrorType.SubjectIdChanged);
    }

    public void create(OpenMRSFullEncounter openMRSEncounter) {

    }

    public void processSubjectIdNotFound() {

    }
}