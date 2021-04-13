package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
public class AvniEnrolmentService {
    @Autowired
    private AvniEnrolmentRepository avniEnrolmentRepository;
    @Autowired
    private OpenMRSEncounterMapper openMRSEncounterMapper;

    public Enrolment getEnrolment(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), splitEncounter.getOpenMRSEncounterUuid());
        return avniEnrolmentRepository.getEnrolment(metaData.getAvniMappedName(splitEncounter.getFormConceptSetUuid()), obsCriteria);
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
        Enrolment[] enrolments = avniEnrolmentRepository.getEnrolments(subjectExternalId, metaData.getAvniMappedName(splitEncounter.getFormConceptSetUuid()));
        return Arrays.stream(enrolments).min((o1, o2) -> {
            DateTime encounterDateTime = new DateTime(splitEncounter.getOpenMRSEncounterDateTime());
            DateTime enrolment1DateTime = new DateTime(o1.getEnrolmentDateTime());
            DateTime enrolment2DateTime = new DateTime(o2.getEnrolmentDateTime());
            return Minutes.minutesBetween(encounterDateTime, enrolment1DateTime).compareTo(Minutes.minutesBetween(encounterDateTime, enrolment2DateTime));
        }).orElse(null);
    }

    public void createEmptyEnrolmentFor(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = openMRSEncounterMapper.mapToEmptyAvniEnrolment(bahmniSplitEncounter, metaData, avniPatient);
        avniEnrolmentRepository.create(enrolment);
    }
}