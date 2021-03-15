package org.bahmni_avni_integration.mapper.bahmni;

import org.aspectj.asm.internal.ProgramElement;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenMRSEncounterMapper {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public GeneralEncounter mapToAvniEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = new GeneralEncounter();
        encounter.setEncounterDateTime(FormatAndParseUtil.fromIsoDateString(splitEncounter.getGetOpenMRSEncounterDateTime()));
        encounter.setEncounterType(bahmniEncounterToAvniEncounterMetaData.getAvniEncounterTypeName(splitEncounter.getFormConceptSetUuid()));
        encounter.setSubjectId(avniPatient.getSubjectExternalId());
        splitEncounter.getObservations().forEach(openMRSObservation -> {
            MappingMetaData conceptMapping = mappingMetaDataRepository.getConceptMappingByOpenMRSConcept(openMRSObservation.getConceptUuid());
            if (ObsDataType.Coded.equals(conceptMapping.getDataTypeHint())) {
                MappingMetaData answerConceptMapping = mappingMetaDataRepository.getConceptMappingByOpenMRSConcept((String) openMRSObservation.getValue());
                encounter.addObservation(conceptMapping.getAvniValue(), answerConceptMapping.getAvniValue());
            } else {
                encounter.addObservation(conceptMapping.getAvniValue(), openMRSObservation.getValue());
            }
        });
        encounter.addObservation(bahmniEncounterToAvniEncounterMetaData.getBahmniEntityUuidConcept(), splitEncounter.getOpenMRSEncounterUuid());
        encounter.setEmptyCancelObservations();
        return encounter;
    }

    public void mapToAvniEnrolment(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = new Enrolment();
        enrolment.setSubjectId(avniPatient.getSubjectExternalId());
//        enrolment.setEnrolmentDateTime();
//        enrolment.setProgram();
    }
}