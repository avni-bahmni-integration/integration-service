package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSSaveObservation;
import org.avni_integration_service.bahmni.contract.OpenMRSUuidHolder;
import org.avni_integration_service.bahmni.contract.OpenMRSVisit;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
public class EnrolmentMapperExternalTest {

    @Autowired
    private EnrolmentMapper enrolmentMapper;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private ConstantsRepository constantsRepository;

    @Autowired
    BahmniMappingGroup bahmniMappingGroup;
    @Autowired
    BahmniMappingType bahmniMappingType;

    private String existingGroupUuid = "22dc3419-b8e5-4316-bf4d-39b9aa743164";
    private String program = "Mother";
    private String patientUuid = "410297f0-945d-4df8-9fa5-ead1d12dc3e5";
    private String enrolmentUuid = "fb6c59c6-cbb5-4c65-8d7e-99019fdb2490";
    private String avniIdObsUuid = "572dd179-e7f5-4bcc-81f6-2ae98a9f1c40";


    @Test
    public void mapEnrolmentToEncounter() {
        var metaData = mappingService.findAll(bahmniMappingGroup.observation, bahmniMappingType.concept);
        var enrolment = new Enrolment();
        enrolment.setProgram(program);
        enrolment.setUuid(enrolmentUuid);
        var avniObservations = new LinkedHashMap<String, Object>();
        int numberOfBabies = 4;
        avniObservations.put("Number of babies", numberOfBabies);
        enrolment.setObservations(avniObservations);
        var formConcept = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment, bahmniMappingType.communityEnrolmentBahmniForm, program);

        enrolment.set("Enrolment datetime", FormatAndParseUtil.toISODateTimeString(new Date()));

        OpenMRSVisit openMRSVisit = new OpenMRSVisit();
        openMRSVisit.setStartDatetime(FormatAndParseUtil.toISODateTimeString(new Date()));

        var openMRSEncounter = enrolmentMapper.mapEnrolmentToEnrolmentEncounter(enrolment,
                patientUuid,
                openMRSVisit,
                constantsRepository.findAllConstants());

        var groupObs = openMRSEncounter.getObservations().get(0);
        assertEquals(formConcept, groupObs.getConcept());

        var avniIdConcept = mappingService.getBahmniValueForAvniIdConcept();
        var avniIdObs = getGroupMember(avniIdConcept, groupObs);
        assertNull(avniIdObs.getUuid());
        assertEquals(enrolment.getUuid(), avniIdObs.getValue());

        var numberOfBabiesObs = getGroupMember(metaData.getBahmniValueForAvniValue("Number of babies"), groupObs);
        assertNull(numberOfBabiesObs.getUuid());
        assertEquals(numberOfBabies, numberOfBabiesObs.getValue());
    }

    @Test
    public void mapEnrolmentToExistingEncounter() {
        var metaData = mappingService.findAll(bahmniMappingGroup.observation, bahmniMappingType.concept);
        var enrolment = new Enrolment();
        enrolment.setUuid(enrolmentUuid);
        enrolment.setProgram(program);
        var avniObservations = new LinkedHashMap<String, Object>();
        int numberOfBabies = 4;
        avniObservations.put("Number of babies", numberOfBabies);
        enrolment.setObservations(avniObservations);
        var formConcept = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment, bahmniMappingType.communityEnrolmentBahmniForm, program);

        var openMRSEncounter = enrolmentMapper.mapEnrolmentToExistingEnrolmentEncounter(getExistingEncounter(), enrolment,
                constantsRepository.findAllConstants());

        var observations = openMRSEncounter.getObservations();
        var groupObs = observations.get(0);
        assertEquals(1, observations.size());
        assertEquals(formConcept, groupObs.getConcept());
        assertEquals(existingGroupUuid, groupObs.getUuid());

        var avniIdConcept = mappingService.getBahmniValueForAvniIdConcept();
        var avniIdObs = getGroupMember(avniIdConcept, groupObs);
        assertEquals(avniIdObsUuid, avniIdObs.getUuid());
        assertEquals(enrolmentUuid, avniIdObs.getValue());

        var numberOfBabiesObs = getGroupMember(metaData.getBahmniValueForAvniValue("Number of babies"), groupObs);
        assertEquals(numberOfBabies, numberOfBabiesObs.getValue());
    }

    private OpenMRSFullEncounter getExistingEncounter() {
        OpenMRSFullEncounter encounter = new OpenMRSFullEncounter();
        OpenMRSUuidHolder patient = new OpenMRSUuidHolder();
        patient.setUuid(patientUuid);
        var formConceptUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment, bahmniMappingType.communityEnrolmentBahmniForm, program);
        var avniIdConcept = mappingService.getBahmniValueForAvniIdConcept();
        encounter.setPatient(patient);
        encounter.setAny("obs", List.of(
                Map.of("uuid", existingGroupUuid,
                        "concept", Map.of("uuid", formConceptUuid),
                        "voided", false,
                        "groupMembers", List.of(createPrimitiveObservation(avniIdObsUuid, avniIdConcept, enrolmentUuid)))));
        return encounter;
    }

    private OpenMRSSaveObservation getGroupMember(String conceptUuid, OpenMRSSaveObservation groupObs) {
        return groupObs.getGroupMembers().stream()
                .filter(o -> o.getConcept().equals(conceptUuid)).findFirst().orElse(null);
    }

    private Map<String, Object> createPrimitiveObservation(String uuid, String conceptUuid, Object value) {
        Map<String, Object> observation = Map.of(
                "uuid", uuid,
                "concept", Map.of("uuid", conceptUuid),
                "value", value,
                "voided", false
        );
        return observation;
    }

}
