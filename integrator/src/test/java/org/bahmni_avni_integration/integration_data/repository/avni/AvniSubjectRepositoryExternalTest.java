package org.bahmni_avni_integration.integration_data.repository.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AvniSubjectRepositoryExternalTest {
    @Autowired
    AvniSubjectRepository avniSubjectRepository;

    @Test
    public void getSubjects() {
        GregorianCalendar calendar = new GregorianCalendar(1900, 0, 1);
        SubjectsResponse response = avniSubjectRepository.getSubjects(calendar.getTime(), "Individual");
        Subject[] subjects = response.getContent();
        assertNotEquals(0, subjects.length);
        assertNotNull(subjects[0].getLastModifiedDate());
    }
}