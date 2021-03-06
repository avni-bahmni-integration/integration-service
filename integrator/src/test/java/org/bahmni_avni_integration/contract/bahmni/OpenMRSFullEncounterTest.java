package org.bahmni_avni_integration.contract.bahmni;

import org.bahmni_avni_integration.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OpenMRSFullEncounterTest {
    @Test
    public void getEncounter() throws URISyntaxException {
        OpenMRSFullEncounter openMRSFullEncounter = TestUtils.readResource("fullEncounter.json", OpenMRSFullEncounter.class);
        assertNotNull(openMRSFullEncounter);
        List<OpenMRSObservation> leafObservations = openMRSFullEncounter.getLeafObservations();
        assertNotEquals(0, leafObservations.size());
    }

    @Test
    public void getEncounter2() throws URISyntaxException {
        OpenMRSFullEncounter openMRSFullEncounter = TestUtils.readResource("fullEncounter2.json", OpenMRSFullEncounter.class);
        assertNotNull(openMRSFullEncounter);

        List<String> forms = openMRSFullEncounter.getForms();
        assertEquals(2, forms.size());
        assertEquals("c36a7537-3f10-11e4-adec-0800271c1b75", forms.get(0));
        assertEquals("c393fd1d-3f10-11e4-adec-0800271c1b75", forms.get(1));

        List<OpenMRSObservation> leafObservations = openMRSFullEncounter.getLeafObservations(forms.get(0));
        assertNotEquals(0, leafObservations.size());

        leafObservations = openMRSFullEncounter.getLeafObservations(forms.get(1));
        assertNotEquals(0, leafObservations.size());
    }
}