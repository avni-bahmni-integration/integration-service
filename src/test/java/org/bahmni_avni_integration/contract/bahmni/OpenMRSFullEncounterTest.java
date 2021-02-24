package org.bahmni_avni_integration.contract.bahmni;

import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenMRSFullEncounterTest {
    @Test
    public void getEncounter() throws URISyntaxException {
        String resourceName = "fullEncounter.json";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        String absolutePath = file.getAbsolutePath();
        OpenMRSFullEncounter openMRSFullEncounter = ObjectJsonMapper.readValue(new File(absolutePath), OpenMRSFullEncounter.class);
        assertNotNull(openMRSFullEncounter);
        List<OpenMRSObservation> leafObservations = openMRSFullEncounter.getLeafObservations();
        assertNotEquals(0, leafObservations.size());
    }
}