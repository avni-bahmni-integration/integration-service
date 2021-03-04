package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ObservationMapperExternalTest {
    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Test
    public void checkVoidingOfCodedAnswers() {
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
                (Map<String, Object>) enrolment.get("observations"));
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(reducedLiquor.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertTrue(observation.isVoided());
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
                (Map<String, Object>) enrolment.get("observations"));
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(numberOfBabies.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertTrue(observation.isVoided());
    }

    @Test
    @Disabled
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
                (Map<String, Object>) enrolment.get("observations"));
        OpenMRSSaveObservation observation = observations.stream()
                .filter(o -> o.getUuid().equals(numberOfBabies.get("uuid"))).findFirst().orElse(null);

        assertNotNull(observation);
        assertFalse(observation.isVoided());
        assertEquals(observation.getValue(), 4);
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