package org.avni_integration_service.mapper.bahmni;

import org.avni_integration_service.contract.avni.AvniBaseContract;
import org.avni_integration_service.contract.avni.Enrolment;
import org.avni_integration_service.contract.avni.GeneralEncounter;
import org.avni_integration_service.contract.avni.ProgramEncounter;
import org.avni_integration_service.contract.bahmni.OpenMRSDefaultEncounter;
import org.avni_integration_service.contract.bahmni.OpenMRSFullEncounter;
import org.avni_integration_service.contract.bahmni.OpenMRSObservation;
import org.avni_integration_service.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.avni_integration_service.integration_data.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenMRSEncounterMapper {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public GeneralEncounter mapToAvniEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = new GeneralEncounter();
        encounter.setEncounterDateTime(FormatAndParseUtil.fromIsoDateString(splitEncounter.getOpenMRSEncounterDateTime()));
        encounter.setEncounterType(bahmniEncounterToAvniEncounterMetaData.getAvniMappedName(splitEncounter.getFormConceptSetUuid()));
        encounter.setSubjectId(avniPatient.getSubjectId());
        addObservations(splitEncounter, encounter, bahmniEncounterToAvniEncounterMetaData);
        encounter.setEmptyCancelObservations();
        encounter.setVoided(splitEncounter.isVoided());
        return encounter;
    }

    public GeneralEncounter mapToAvniEncounter(OpenMRSFullEncounter openMRSFullEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = new GeneralEncounter();
        encounter.setEncounterDateTime(FormatAndParseUtil.fromIsoDateString(openMRSFullEncounter.getEncounterDatetime()));
        encounter.setEncounterType(bahmniEncounterToAvniEncounterMetaData.getLabEncounterTypeMapping().getAvniValue());
        encounter.setSubjectId(avniPatient.getSubjectId());
        addObservations(openMRSFullEncounter.getLeafObservations(), encounter, bahmniEncounterToAvniEncounterMetaData, openMRSFullEncounter.getUuid());
        encounter.setEmptyCancelObservations();
        encounter.setVoided(openMRSFullEncounter.isVoided());
        return encounter;
    }

    public GeneralEncounter mapDrugOrderEncounterToAvniEncounter(OpenMRSFullEncounter openMRSFullEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient, OpenMRSDefaultEncounter defaultEncounter) {
        GeneralEncounter encounter = new GeneralEncounter();
        encounter.setEncounterDateTime(FormatAndParseUtil.fromIsoDateString(openMRSFullEncounter.getEncounterDatetime()));
        encounter.setEncounterType(bahmniEncounterToAvniEncounterMetaData.getDrugOrderEncounterTypeMapping().getAvniValue());
        encounter.setSubjectId(avniPatient.getSubjectId());
        List<String> drugOrders = openMRSFullEncounter.getDrugOrders(defaultEncounter);
        encounter.addObservation(bahmniEncounterToAvniEncounterMetaData.getBahmniEntityUuidConcept(), openMRSFullEncounter.getUuid());
        encounter.addObservation(bahmniEncounterToAvniEncounterMetaData.getDrugOrderConceptMapping().getAvniValue(), String.join(";   ", drugOrders));
        encounter.setEmptyCancelObservations();
        encounter.setVoided(openMRSFullEncounter.isVoided());
        return encounter;
    }

    private void addObservations(BahmniSplitEncounter splitEncounter, AvniBaseContract avniBaseContract, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData) {
        addObservations(splitEncounter.getObservations(), avniBaseContract, bahmniEncounterToAvniEncounterMetaData, splitEncounter.getOpenMRSEncounterUuid());
    }

    private void addObservations(List<OpenMRSObservation> observations, AvniBaseContract avniBaseContract, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, String encounterUuid) {
        observations.forEach(openMRSObservation -> {
            MappingMetaData conceptMapping = mappingMetaDataRepository.getConceptMappingByOpenMRSConcept(openMRSObservation.getConceptUuid(), bahmniEncounterToAvniEncounterMetaData, true);
            if (conceptMapping == null) return;

            if (conceptMapping.isCoded()) {
                MappingMetaData answerConceptMapping = mappingMetaDataRepository.getConceptMappingByOpenMRSConcept((String) openMRSObservation.getValue(), bahmniEncounterToAvniEncounterMetaData, false);
                avniBaseContract.addObservation(conceptMapping.getAvniValue(), answerConceptMapping.getAvniValue());
            } else {
                avniBaseContract.addObservation(conceptMapping.getAvniValue(), openMRSObservation.getValue());
            }
        });
        avniBaseContract.addObservation(bahmniEncounterToAvniEncounterMetaData.getBahmniEntityUuidConcept(), encounterUuid);
    }

    public Enrolment mapToAvniEnrolment(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = new Enrolment();
        enrolment.setSubjectId(avniPatient.getSubjectId());
        enrolment.setEnrolmentDateTime(FormatAndParseUtil.fromIsoDateString(splitEncounter.getOpenMRSEncounterDateTime()));
        enrolment.setProgram(metaData.getAvniMappedName(splitEncounter.getFormConceptSetUuid()));
        enrolment.setEmptyExitObservations();
        addObservations(splitEncounter, enrolment, metaData);
        return enrolment;
    }

    public ProgramEncounter mapToAvniProgramEncounter(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, Enrolment enrolment) {
        ProgramEncounter programEncounter = new ProgramEncounter();
        programEncounter.setProgramEnrolment(enrolment.getUuid());
        programEncounter.setEncounterDateTime(FormatAndParseUtil.fromIsoDateString(splitEncounter.getOpenMRSEncounterDateTime()));
        programEncounter.setEncounterType(metaData.getAvniMappedName(splitEncounter.getFormConceptSetUuid()));
        addObservations(splitEncounter, programEncounter, metaData);
        programEncounter.setEmptyCancelObservations();
        return programEncounter;
    }

    public Enrolment mapToEmptyAvniEnrolment(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = new Enrolment();
        enrolment.setSubjectId(avniPatient.getSubjectId());
        enrolment.setEnrolmentDateTime(FormatAndParseUtil.fromIsoDateString(splitEncounter.getOpenMRSEncounterDateTime()));
        enrolment.setProgram(metaData.getAvniProgramName(splitEncounter.getFormConceptSetUuid()));
        enrolment.setEmptyExitObservations();
        enrolment.addObservation(metaData.getBahmniEntityUuidConcept(), splitEncounter.getOpenMRSEncounterUuid());
        return enrolment;
    }
}
