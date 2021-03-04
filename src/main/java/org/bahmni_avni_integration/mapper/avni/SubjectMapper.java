package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class SubjectMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ObservationMapper observationMapper;

    public SubjectMapper(MappingMetaDataRepository mappingMetaDataRepository, ObservationMapper observationMapper) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.observationMapper = observationMapper;
    }

    public OpenMRSEncounter mapSubjectToEncounter(Subject subject, String patientUuid, String encounterTypeUuid, Constants constants) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterDatetime(FormatAndParseUtil.toISODateStringWithTimezone(new Date()));
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));

        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        observationMapper.mapObservations((LinkedHashMap<String, Object>) subject.get("observations"), openMRSEncounter);
        mapSubjectUuid(subject, openMRSEncounter);
//        story-todo - map audit observations
        LinkedHashMap<String, Object> avniAuditObservations = (LinkedHashMap<String, Object>) subject.get("audit");
        return openMRSEncounter;
    }

    private void mapSubjectUuid(Subject subject, OpenMRSEncounter openMRSEncounter) {
        MappingMetaData subjectUuidMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(subjectUuidMapping.getBahmniValue(), (String) subject.getUuid(), ObsDataType.Text));
    }
}