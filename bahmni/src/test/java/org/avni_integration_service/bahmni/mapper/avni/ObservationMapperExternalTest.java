package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSSaveObservation;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.bahmni.MappingMetaDataCollection;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
public class ObservationMapperExternalTest {
    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private MappingService mappingService;

    @Test
    public void checkVoidingOfMultiSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);

        Map<String, Object> noProblem = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Obstetrics history"),
                metaData.getBahmniValueForAvniValue("No problem")
        );
        Map<String, Object> reducedLiquor = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Obstetrics history"),
                metaData.getBahmniValueForAvniValue("Reduced liquor")
        );
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(noProblem, reducedLiquor));

        var avniObservations = new LinkedHashMap<String, Object>();
        avniObservations.put("Obstetrics history", List.of("No problem"));
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(reducedLiquor.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertNotNull(observation.getUuid());
        assertTrue(observation.isVoided());
    }

    @Test
    public void checkVoidingOfSingleSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);

        Map<String, Object> ancRegisteredYes = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("ANC registered"),
                metaData.getBahmniValueForAvniValue("Yes")
        );
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(ancRegisteredYes));

        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("ANC registered", "No");
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());
        OpenMRSSaveObservation existingObs = observations.stream()
                .filter(o -> o.getUuid() != null && o.getUuid().equals(ancRegisteredYes.get("uuid"))).findFirst().orElse(null);
        OpenMRSSaveObservation newObs = observations.stream()
                .filter(o -> o.getValue() != null && o.getValue().equals(metaData.getBahmniValueForAvniValue("No"))).findFirst().orElse(null);

        assertNotNull(existingObs);
        assertNotNull(existingObs.getUuid());
        assertTrue(existingObs.isVoided());

        assertNotNull(newObs);
        assertNull(newObs.getUuid());
        assertFalse(newObs.isVoided());
    }

    @Test
    public void checkVoidingOfObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);

        Map<String, Object> numberOfBabies = createPrimitiveObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Number of babies"),
                2
        );

        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(numberOfBabies));

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                new LinkedHashMap<>(), List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(numberOfBabies.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertTrue(observation.isVoided());
    }

    @Test
    public void checkUpdateOfObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);

        Map<String, Object> numberOfBabies = createPrimitiveObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Number of babies"),
                2);

        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(numberOfBabies));

        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Number of babies", 4);

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(numberOfBabies.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertFalse(observation.isVoided());
        assertEquals(observation.getValue(), 4);
    }

    @Test
    public void checkAddingOfPrimitiveObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of());

        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Number of babies", 4);

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());

        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue("Number of babies"))).findFirst().orElse(null);
        assertNotNull(observation);
        assertNull(observation.getUuid());
        assertFalse(observation.isVoided());
        assertEquals(observation.getValue(), 4);
    }

    @Test
    public void checkAddingOfMultiSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        Map<String, Object> obsHistoryNoProblem = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Obstetrics history"),
                metaData.getBahmniValueForAvniValue("No problem")
        );
        Map<String, Object> obsHistoryStillBirth = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Obstetrics history"),
                metaData.getBahmniValueForAvniValue("Still Birth")
        );
        Map<String, Object> abdominalProblemsNoProblem = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Any abdominal problems"),
                metaData.getBahmniValueForAvniValue("No problem")
        );
        openMRSFullEncounter.setAny("obs", List.of(abdominalProblemsNoProblem, obsHistoryNoProblem, obsHistoryStillBirth));

        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Obstetrics history", List.of("No problem", "Reduced liquor"));
        avniObservations.put("Any abdominal problems", List.of("No problem"));

        var updateObservations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());

        assertEquals(4, updateObservations.size());
        OpenMRSSaveObservation absProblemResult = updateObservations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue("Any abdominal problems"))).findFirst().orElse(null);
        assertFalse(absProblemResult.isVoided());
        assertEquals(abdominalProblemsNoProblem.get("uuid"), absProblemResult.getUuid());

        var obsHistoryObservations = updateObservations
                .stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue("Obstetrics history")))
                .collect(Collectors.toList());
        assertEquals(3, obsHistoryObservations.size());
        OpenMRSSaveObservation obsHistoryNoProblemResult = obsHistoryObservations.stream()
                .filter(o -> o.getValue() != null && o.getValue().equals(metaData.getBahmniValueForAvniValue("No problem"))).findFirst().orElse(null);
        OpenMRSSaveObservation newObs = obsHistoryObservations.stream()
                .filter(o -> o.getValue() != null && o.getValue().equals(metaData.getBahmniValueForAvniValue("Reduced liquor"))).findFirst().orElse(null);
        OpenMRSSaveObservation stillBirthResult = obsHistoryObservations.stream()
                .filter(o -> o.getUuid() != null && o.getUuid().equals(obsHistoryStillBirth.get("uuid"))).findFirst().orElse(null);

        assertFalse(obsHistoryNoProblemResult.isVoided());
        assertEquals(obsHistoryNoProblem.get("uuid"), obsHistoryNoProblemResult.getUuid());
        assertTrue(stillBirthResult.isVoided());
        assertEquals(obsHistoryStillBirth.get("uuid"), stillBirthResult.getUuid());
        assertNull(stillBirthResult.getValue());
        assertNull(newObs.getUuid());
    }

    @Test
    public void checkAddingOfSingleSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        Map<String, Object> obsHistoryNoProblem = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Obstetrics history"),
                metaData.getBahmniValueForAvniValue("No problem")
        );
        Map<String, Object> abdominalProblemsNoProblem = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Any abdominal problems"),
                metaData.getBahmniValueForAvniValue("No problem")
        );
        openMRSFullEncounter.setAny("obs", List.of(abdominalProblemsNoProblem, obsHistoryNoProblem));

        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Obstetrics history", "No problem");
        avniObservations.put("Any abdominal problems", List.of("No problem"));

        var updateObservations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());

        assertEquals(2, updateObservations.size());
        OpenMRSSaveObservation absProblemResult = updateObservations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue("Any abdominal problems"))).findFirst().orElse(null);
        assertFalse(absProblemResult.isVoided());
        assertEquals(abdominalProblemsNoProblem.get("uuid"), absProblemResult.getUuid());

        OpenMRSSaveObservation obsHistoryNoProblemResult = updateObservations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue("Obstetrics history"))).findFirst().orElse(null);

        assertFalse(obsHistoryNoProblemResult.isVoided());
        assertEquals(obsHistoryNoProblem.get("uuid"), obsHistoryNoProblemResult.getUuid());
    }

    @Test
    public void checkUpdateOfEmptyTextObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);

        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of());

        var avniObservations = new LinkedHashMap<String, Object>();
        String avniTextConcept = "Reason for skipping height capture.";
        avniObservations.put(avniTextConcept, "  ");
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                avniObservations, List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue(avniTextConcept))).findFirst().orElse(null);

        assertNull(observation);
    }

    @Test
    public void checkAddingOfEmptyTextObservations() {
        MappingMetaDataCollection metaData = mappingService.findAll(BahmniMappingGroup.Observation, MappingType.Concept);
        var avniObservations = new LinkedHashMap<String, Object>();
        String avniTextConcept = "Reason for skipping height capture.";
        avniObservations.put(avniTextConcept, "  ");

        var observations = observationMapper.mapObservations(avniObservations);

        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue(avniTextConcept))).findFirst().orElse(null);
        assertNull(observation);
    }


    private Map<String, Object> createCodedObservation(UUID uuid, String conceptUuid, String answerUuid) {
        Map<String, Object> observation = Map.of(
                "uuid", uuid.toString(),
                "concept", Map.of("uuid", conceptUuid),
                "value", Map.of("uuid", answerUuid),
                "voided", false
        );
        return observation;
    }

    private Map<String, Object> createPrimitiveObservation(UUID uuid, String conceptUuid, Object value) {
        Map<String, Object> observation = Map.of(
                "uuid", uuid.toString(),
                "concept", Map.of("uuid", conceptUuid),
                "value", value,
                "voided", false
        );
        return observation;
    }

}
