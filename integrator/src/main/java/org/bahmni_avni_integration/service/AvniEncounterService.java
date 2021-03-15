package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
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
    private ErrorService errorService; // use error repository as the service in the middle is not adding value
    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    public void update(BahmniSplitEncounter bahmniSplitEncounter, GeneralEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(bahmniSplitEncounter, bahmniEncounterToAvniEncounterMetaData, avniPatient);
        avniEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public GeneralEncounter getGeneralEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        LinkedHashMap<String, Object> encounterCriteria = new LinkedHashMap<>();
        encounterCriteria.put("encounterType", metaData.getAvniEncounterTypeName(openMRSEncounter.getEncounterType().getUuid()));
        encounterCriteria.put(metaData.getBahmniEntityUuidConcept(), openMRSEncounter.getUuid());
        return avniEncounterRepository.getEncounter(encounterCriteria);
    }

    public void processSubjectIdChanged(GeneralEncounter existingEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        errorService.errorOccurred(existingEncounter, ErrorType.SubjectIdChanged);
    }

    public void create(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        MappingMetaData mapping = metaData.getEncounterMappingFor(splitEncounter.getOpenMRSEncounterUuid());
        switch (mapping.getMappingGroup()) {
            case GeneralEncounter:
                GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(splitEncounter, metaData, avniPatient);
                avniEncounterRepository.create(encounter);
                break;
            case ProgramEnrolment:
                openMRSEncounterMapper.mapToAvniEnrolment(splitEncounter, metaData, avniPatient);
                break;
            case ProgramEncounter:
                break;
        }
    }

    public void processSubjectIdNotFound(OpenMRSFullEncounter openMRSEncounter) {
        ErrorRecord errorRecord = errorRecordRepository.findByBahmniEntityTypeAndEncounterExternalIdAndErrorType(BahmniEntityType.Encounter, openMRSEncounter.getUuid(), ErrorType.NoSubjectWithExternalId);
        if (errorRecord != null) return;

        errorRecord = new ErrorRecord();
        errorRecord.setErrorType(ErrorType.NoSubjectWithExternalId);
        errorRecord.setBahmniEntityType(BahmniEntityType.Encounter);
        errorRecord.setSubjectPatientExternalId(openMRSEncounter.getPatient().getUuid());
        errorRecord.setEncounterExternalId(openMRSEncounter.getUuid());
        errorRecordRepository.save(errorRecord);
    }
}