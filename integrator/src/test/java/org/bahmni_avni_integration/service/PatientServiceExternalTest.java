package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.BaseExternalTest;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.javatuples.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.GregorianCalendar;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PatientServiceExternalTest extends BaseExternalTest {
    @Autowired
    private AvniSubjectRepository avniSubjectRepository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Test
    @Disabled("Disabling for now as it needs more thinking on how to test this correctly")
    public void createAndUpdatePatient() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(1980, Calendar.JANUARY, 1);
        Subject[] individuals = avniSubjectRepository.getSubjects(gregorianCalendar.getTime(), "Individual");
        SubjectToPatientMetaData metaData = mappingMetaDataService.getForSubjectToPatient();
        Pair<OpenMRSUuidHolder, OpenMRSFullEncounter> patientEncounter = patientService.findSubject(individuals[0], getConstants(), metaData);
        assertNotNull(patientEncounter.getValue0());
        assertNull(patientEncounter.getValue1());
        OpenMRSFullEncounter subjectEncounter = patientService.createSubject(individuals[0], patientEncounter.getValue0(), metaData, getConstants());
        assertNotNull(subjectEncounter);
        patientService.updateSubject(subjectEncounter, patientEncounter.getValue0(), individuals[0], metaData, getConstants());
    }
}