package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.mapper.OpenMRSEncounterMapper;
import org.avni_integration_service.contract.avni.Enrolment;
import org.avni_integration_service.contract.avni.GeneralEncounter;
import org.avni_integration_service.contract.repository.AvniEnrolmentRepository;
import org.avni_integration_service.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.avni_integration_service.bahmni.repository.BahmniSplitEncounter;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class AvniEnrolmentService {
    @Autowired
    private AvniEnrolmentRepository avniEnrolmentRepository;
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;

    public Enrolment getEnrolment(BahmniSplitEncounter splitEncounter, String subjectId, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = new HashMap<>();
        return avniEnrolmentRepository.getEnrolment(subjectId, metaData.getAvniMappedName(splitEncounter.getFormConceptSetUuid()), obsCriteria);
    }

    public Enrolment update(BahmniSplitEncounter splitEncounter, Enrolment existingEnrolment, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = openMRSEncounterMapper.mapToAvniEnrolment(splitEncounter, metaData, avniPatient);
        return avniEnrolmentRepository.update(existingEnrolment.getUuid(), enrolment);
    }

    public void create(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = openMRSEncounterMapper.mapToAvniEnrolment(splitEncounter, metaData, avniPatient);
        avniEnrolmentRepository.create(enrolment);
    }

    public Enrolment getMatchingEnrolment(String subjectExternalId, BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Enrolment[] enrolments = avniEnrolmentRepository.getEnrolments(subjectExternalId, metaData.getAvniProgramName(splitEncounter.getFormConceptSetUuid()));
        return Arrays.stream(enrolments).min((o1, o2) -> {
            DateTime encounterDateTime = new DateTime(splitEncounter.getOpenMRSEncounterDateTime());
            DateTime enrolment1DateTime = new DateTime(o1.getEnrolmentDateTime());
            DateTime enrolment2DateTime = new DateTime(o2.getEnrolmentDateTime());
            return Minutes.minutesBetween(encounterDateTime, enrolment1DateTime).compareTo(Minutes.minutesBetween(encounterDateTime, enrolment2DateTime));
        }).orElse(null);
    }

    public Enrolment createEmptyEnrolmentFor(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = openMRSEncounterMapper.mapToEmptyAvniEnrolment(bahmniSplitEncounter, metaData, avniPatient);
        return avniEnrolmentRepository.create(enrolment);
    }
}
