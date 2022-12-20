package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.domain.SubjectsResponse;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
class AvniSubjectRepositoryExternalTest {
    @Autowired
    AvniSubjectRepository avniSubjectRepository;

    @Test
    public void getSubjects() {
        GregorianCalendar calendar = new GregorianCalendar(1900, Calendar.JANUARY, 1);
        SubjectsResponse response = avniSubjectRepository.getSubjects(calendar.getTime(), "Individual");
        Subject[] subjects = response.getContent();
        assertNotEquals(0, subjects.length);
        assertNotNull(subjects[0].getLastModifiedDate());
    }
}
