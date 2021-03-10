package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
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
public class ObservationMapperExternalTest {
    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Test
    public void checkVoidingOfMultiSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);

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

        Enrolment enrolment = new Enrolment();
        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Obstetrics history", List.of("No problem"));
        enrolment.set("observations", avniObservations);
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(reducedLiquor.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertNotNull(observation.getUuid());
        assertTrue(observation.isVoided());
    }

    @Test
    public void checkVoidingOfSingleSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);

        Map<String, Object> ancRegisteredYes = createCodedObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("ANC registered"),
                metaData.getBahmniValueForAvniValue("Yes")
        );
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(ancRegisteredYes));

        Enrolment enrolment = new Enrolment();
        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("ANC registered", "No");
        enrolment.set("observations", avniObservations);
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
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
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);

        Map<String, Object> numberOfBabies = createPrimitiveObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Number of babies"),
                2
        );

        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(numberOfBabies));

        Enrolment enrolment = new Enrolment();
        enrolment.set("observations", new LinkedHashMap<String, Object>());
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(numberOfBabies.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertTrue(observation.isVoided());
    }

    @Test
    public void checkUpdateOfObservations() {
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);

        Map<String, Object> numberOfBabies = createPrimitiveObservation(
                UUID.randomUUID(),
                metaData.getBahmniValueForAvniValue("Number of babies"),
                2
        );

        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of(numberOfBabies));

        Enrolment enrolment = new Enrolment();
        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Number of babies", 4);
        enrolment.set("observations", avniObservations);

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(numberOfBabies.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertFalse(observation.isVoided());
        assertEquals(observation.getValue(), 4);
    }

    @Test
    public void checkAddingOfPrimitiveObservations() {
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
        OpenMRSFullEncounter openMRSFullEncounter = new OpenMRSFullEncounter();
        openMRSFullEncounter.setAny("obs", List.of());

        Enrolment enrolment = new Enrolment();
        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Number of babies", 4);
        enrolment.set("observations", avniObservations);

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getConcept().equals(metaData.getBahmniValueForAvniValue("Number of babies"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertNull(observation.getUuid());
        assertFalse(observation.isVoided());
        assertEquals(observation.getValue(), 4);
    }

    @Test
    public void checkAddingOfMultiSelectCodedObservations() {
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
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

        Enrolment enrolment = new Enrolment();
        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Obstetrics history", List.of("No problem", "Reduced liquor"));
        avniObservations.put("Any abdominal problems", List.of("No problem"));
        enrolment.set("observations", avniObservations);

        var updateObservations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
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
        MappingMetaDataCollection metaData = mappingMetaDataRepository.findAll(MappingGroup.Observation, MappingType.Concept);
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

        Enrolment enrolment = new Enrolment();
        Map<String, Object> avniObservations = new LinkedHashMap<>();
        avniObservations.put("Obstetrics history", "No problem");
        avniObservations.put("Any abdominal problems", List.of("No problem"));
        enrolment.set("observations", avniObservations);

        var updateObservations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                openMRSFullEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"), List.of());
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


    private Map<String, Object> createCodedObservation(UUID uuid, String conceptUuid, String answerUuid) {
        Map<String, Object> observation = Map.of(
                "uuid", uuid.toString(),
                "concept", Map.of("uuid", conceptUuid),
                "value", Map.of("uuid", answerUuid)
        );
        return observation;
    }

    private Map<String, Object> createPrimitiveObservation(UUID uuid, String conceptUuid, Object value) {
        Map<String, Object> observation = Map.of(
                "uuid", uuid.toString(),
                "concept", Map.of("uuid", conceptUuid),
                "value", value
        );
        return observation;
    }

}