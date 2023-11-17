package org.bahmni_avni_integration.integration_data.repository.bahmni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class BahmniSplitEncounterTest {
    @Test
    public void getMatchingEnrolment() {
        BahmniSplitEncounter bahmniSplitEncounter = new BahmniSplitEncounter(null, null, "2021-10-10T12:00:19.000+0530", new ArrayList<>(), false);
        assertEquals("a", bahmniSplitEncounter.getMatchingEnrolment(new Enrolment[]{enrolment("a", 2021, 8, 10), enrolment("b", 2017, 9, 10)}).getUuid());
        assertEquals("a", bahmniSplitEncounter.getMatchingEnrolment(new Enrolment[]{enrolment("b", 2017, 9, 10), enrolment("a", 2021, 8, 10)}).getUuid());
        assertEquals("b", bahmniSplitEncounter.getMatchingEnrolment(new Enrolment[]{enrolment("b", 2021, 9, 10), enrolment("a", 2021, 8, 10)}).getUuid());
    }

    private Enrolment enrolment(String uuid, int year, int month, int dayOfMonth) {
        Enrolment enrolment = new TestEnrolment();
        enrolment.setUuid(uuid);
        enrolment.setEnrolmentDateTime(new GregorianCalendar(year, month, dayOfMonth).getTime());
        return enrolment;
    }

    class TestEnrolment extends Enrolment {
        private Date enrolmentDateTime;

        @Override
        public Date getEnrolmentDateTime() {
            return enrolmentDateTime;
        }

        @Override
        public void setEnrolmentDateTime(Date enrolmentDateTime) {
            this.enrolmentDateTime = enrolmentDateTime;
        }
    }
}
