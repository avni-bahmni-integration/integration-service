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
        return splitEncounter.getMatchingEnrolment(enrolments);
    }

    public Enrolment createEmptyEnrolmentFor(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        Enrolment enrolment = openMRSEncounterMapper.mapToEmptyAvniEnrolment(bahmniSplitEncounter, metaData, avniPatient);
        return avniEnrolmentRepository.create(enrolment);
    }
}
