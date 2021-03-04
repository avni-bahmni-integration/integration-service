package org.bahmni_avni_integration.mapper.bahmni;

import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.domain.MappingMetaData;
import org.bahmni_avni_integration.domain.ObsDataType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSEncounterMapper {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public GeneralEncounter mapToAvniEncounter(OpenMRSFullEncounter openMRSPatientEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = new GeneralEncounter();
        encounter.setEncounterDateTime(FormatAndParseUtil.fromIsoDateString(openMRSPatientEncounter.getEncounterDatetime()));
        encounter.setEncounterType(bahmniEncounterToAvniEncounterMetaData.getAvniEncounterTypeName(openMRSPatientEncounter.getEncounterType().getUuid()));
        encounter.setSubjectId(avniPatient.getSubjectExternalId());
        openMRSPatientEncounter.getLeafObservations().forEach(openMRSObservation -> {
            MappingMetaData conceptMapping = mappingMetaDataRepository.getConceptMappingByOpenMRSConcept(openMRSObservation.getConceptUuid());
            if (ObsDataType.Coded.equals(conceptMapping.getDataTypeHint())) {
                mappingMetaDataRepository.getConceptMappingByOpenMRSConcept(openMRSObservation.getConceptUuid());
                MappingMetaData answerConceptMapping = mappingMetaDataRepository.getConceptMappingByOpenMRSConcept((String) openMRSObservation.getValue());
                encounter.set(conceptMapping.getAvniValue(), answerConceptMapping.getAvniValue());
            } else {
                encounter.set(conceptMapping.getAvniValue(), openMRSObservation.getValue());
            }
        });
        return encounter;
    }
}