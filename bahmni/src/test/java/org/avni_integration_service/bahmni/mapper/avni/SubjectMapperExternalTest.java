package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSSaveObservation;
import org.avni_integration_service.bahmni.contract.OpenMRSUuidHolder;
import org.avni_integration_service.bahmni.contract.OpenMRSVisit;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Disabled
public class SubjectMapperExternalTest {

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    private ConstantsRepository constantsRepository;

    @Autowired
    DataSource dataSource;

    private String existingGroupUuid = "22dc3419-b8e5-4316-bf4d-39b9aa743164";

    @Test
    public void mapSubjectToEncounter() {
        var metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        var subject = new Subject();
        subject.setUuid("fb6c59c6-cbb5-4c65-8d7e-99019fdb2490");
        var avniObservations = new LinkedHashMap<String, Object>();
        int numberOfBabies = 4;
        avniObservations.put("Number of babies", numberOfBabies);
        subject.set("observations", avniObservations);
        subject.set("Registration date", FormatAndParseUtil.toISODateTimeString(new Date()));
        var formConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.CommunityRegistration_BahmniForm);
        var entityUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        OpenMRSVisit openMRSVisit = new OpenMRSVisit();
        openMRSVisit.setStartDatetime(FormatAndParseUtil.toISODateTimeString(new Date()));
        var openMRSEncounter = subjectMapper.mapSubjectToEncounter(subject,
                "cc0369c8-748c-42cc-a534-5ab40855c3f8",
                "f39ce690-d1c4-4bb3-aa4b-893bdd73e5a1",
                constantsRepository.findAllConstants(),
                openMRSVisit);
        var groupObs = openMRSEncounter.getObservations().get(0);
        assertEquals(formConcept, groupObs.getConcept());
        var entityUuidObs = getGroupMember(entityUuidConcept, groupObs);
        var numberOfBabiesObs = getGroupMember(metaData.getBahmniValueForAvniValue("Number of babies"), groupObs);
        assertEquals(subject.getUuid(), entityUuidObs.getValue());
        assertEquals(numberOfBabies, numberOfBabiesObs.getValue());

    }

    @Test
    public void mapSubjectToExistingEncounter() {
        var metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        var subject = new Subject();
        subject.setUuid("fb6c59c6-cbb5-4c65-8d7e-99019fdb2490");
        var avniObservations = new LinkedHashMap<String, Object>();
        int numberOfBabies = 4;
        avniObservations.put("Number of babies", numberOfBabies);
        subject.set("observations", avniObservations);
        var formConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.CommunityRegistration_BahmniForm);
        var openMRSEncounter = subjectMapper.mapSubjectToExistingEncounter(getExistingEncounter(), subject,
                "cc0369c8-748c-42cc-a534-5ab40855c3f8",
                "f39ce690-d1c4-4bb3-aa4b-893bdd73e5a1",
                constantsRepository.findAllConstants());

        var observations = openMRSEncounter.getObservations();
        var groupObs = observations.get(0);
        assertEquals(1, observations.size());
        assertEquals(formConcept, groupObs.getConcept());
        assertEquals(existingGroupUuid, groupObs.getUuid());
        var numberOfBabiesObs = getGroupMember(metaData.getBahmniValueForAvniValue("Number of babies"), groupObs);
        assertEquals(numberOfBabies, numberOfBabiesObs.getValue());
    }

    private OpenMRSFullEncounter getExistingEncounter() {
        OpenMRSFullEncounter encounter = new OpenMRSFullEncounter();
        OpenMRSUuidHolder patient = new OpenMRSUuidHolder();
        patient.setUuid("e0d4570b-3ca0-439e-a65e-d8ebe95caa14");
        var formConceptUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.CommunityRegistration_BahmniForm);
        encounter.setPatient(patient);
        encounter.setAny("obs", List.of(
                Map.of("uuid", existingGroupUuid,
                        "concept", Map.of("uuid", formConceptUuid),
                        "groupMembers", List.of(),
                        "voided", false)));
        return encounter;
    }

    private OpenMRSSaveObservation getGroupMember(String concept, OpenMRSSaveObservation groupObs) {
        return groupObs.getGroupMembers().stream()
                .filter(o -> o.getConcept().equals(concept)).findFirst().orElse(null);
    }

}
