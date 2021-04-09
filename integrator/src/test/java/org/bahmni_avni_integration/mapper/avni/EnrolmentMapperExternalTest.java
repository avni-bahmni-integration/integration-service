package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EnrolmentMapperExternalTest {

    @Autowired
    private EnrolmentMapper enrolmentMapper;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    private ConstantsRepository constantsRepository;

    @Test
    public void mapEnrolmentToEncounter() {
        var metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        var enrolment = new Enrolment();
        String program = "Mother";
        enrolment.setProgram(program);
        enrolment.setUuid("fb6c59c6-cbb5-4c65-8d7e-99019fdb2490");
        var avniObservations = new LinkedHashMap<String, Object>();
        int numberOfBabies = 4;
        avniObservations.put("Number of babies", numberOfBabies);
        enrolment.set("observations", avniObservations);
        var formConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolment_BahmniForm, program);
        var entityUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniUuidConcept();
        var openMRSEncounter = enrolmentMapper.mapEnrolmentToEncounter(enrolment,
                "cc0369c8-748c-42cc-a534-5ab40855c3f8",
                constantsRepository.findAllConstants());
        var groupObs = openMRSEncounter.getObservations().get(0);
        assertEquals(formConcept, groupObs.getConcept());
        var entityUuidObs = getGroupMember(entityUuidConcept, groupObs);
        var numberOfBabiesObs = getGroupMember(metaData.getBahmniValueForAvniValue("Number of babies"), groupObs);
        assertEquals(enrolment.getUuid(), entityUuidObs.getValue());
        assertEquals(numberOfBabies, numberOfBabiesObs.getValue());

    }

    @Test
    public void mapEnrolmentToExistingEncounter() {
        var metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        var enrolment = new Enrolment();
        enrolment.setUuid("fb6c59c6-cbb5-4c65-8d7e-99019fdb2490");
        String program = "Mother";
        enrolment.setProgram(program);
        var avniObservations = new LinkedHashMap<String, Object>();
        int numberOfBabies = 4;
        avniObservations.put("Number of babies", numberOfBabies);
        enrolment.set("observations", avniObservations);
        var formConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolment_BahmniForm, program);
        var openMRSEncounter = enrolmentMapper.mapEnrolmentToExistingEncounter(getExistingEncounter(), enrolment,
                constantsRepository.findAllConstants());
        var groupObs = openMRSEncounter.getObservations().get(0);
        assertEquals(formConcept, groupObs.getConcept());
        var numberOfBabiesObs = getGroupMember(metaData.getBahmniValueForAvniValue("Number of babies"), groupObs);
        assertEquals(numberOfBabies, numberOfBabiesObs.getValue());
    }

    private OpenMRSFullEncounter getExistingEncounter() {
        OpenMRSFullEncounter encounter = new OpenMRSFullEncounter();
        OpenMRSUuidHolder patient = new OpenMRSUuidHolder();
        patient.setUuid("e0d4570b-3ca0-439e-a65e-d8ebe95caa14");
        encounter.setPatient(patient);
        encounter.setAny("obs", new ArrayList<>());
        return encounter;
    }

    private OpenMRSSaveObservation getGroupMember(String concept, OpenMRSSaveObservation groupObs) {
        return groupObs.getGroupMembers().stream()
                .filter(o -> o.getConcept().equals(concept)).findFirst().orElse(null);
    }

}