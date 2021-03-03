package org.bahmni_avni_integration.contract.bahmni;

import org.bahmni_avni_integration.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenMRSFullEncounterTest {
    @Test
    public void getEncounter() throws URISyntaxException {
        OpenMRSFullEncounter openMRSFullEncounter = TestUtils.readResource("fullEncounter.json", OpenMRSFullEncounter.class);
        assertNotNull(openMRSFullEncounter);
        List<OpenMRSObservation> leafObservations = openMRSFullEncounter.getLeafObservations();
        assertNotEquals(0, leafObservations.size());
    }
}