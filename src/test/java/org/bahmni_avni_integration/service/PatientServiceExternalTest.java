package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.BaseExternalTest;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPostSaveEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.javatuples.Pair;
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
    public void createAndUpdatePatient() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(1980, Calendar.JANUARY, 1);
        Subject[] individuals = avniSubjectRepository.getSubjects(gregorianCalendar.getTime(), "Individual");
        SubjectToPatientMetaData metaData = mappingMetaDataService.getForSubjectToPatient();
        Pair<OpenMRSUuidHolder, OpenMRSEncounter> patientEncounter = patientService.findSubject(individuals[0], getConstants(), metaData);
        assertNotNull(patientEncounter.getValue0());
        assertNull(patientEncounter.getValue1());
        OpenMRSPostSaveEncounter subjectEncounter = patientService.createSubject(individuals[0], patientEncounter.getValue0(), metaData, getConstants());
        assertNotNull(subjectEncounter);
        patientService.updateSubject(patientEncounter.getValue0(), individuals[0], metaData, getConstants());
    }
}