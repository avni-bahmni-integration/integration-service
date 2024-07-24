package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SubjectMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ObservationMapper observationMapper;

    public SubjectMapper(MappingMetaDataRepository mappingMetaDataRepository, ObservationMapper observationMapper) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.observationMapper = observationMapper;
    }

    public OpenMRSEncounter mapSubjectToEncounter(Subject subject, String patientUuid, String encounterTypeUuid, Constants constants, OpenMRSVisit visit) {
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));

        var encounterProvider = new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole));
        openMRSEncounter.addEncounterProvider(encounterProvider);

        var observations = observationMapper.mapObservations((LinkedHashMap<String, Object>) subject.get("observations"));
        observations.add(avniUuidObs(subject.getUuid()));
        observations.add(eventDateObs(subject));
        openMRSEncounter.setObservations(groupObs(observations));
        openMRSEncounter.setEncounterDatetime(MapperUtils.getEventDateTime(subject.getRegistrationDate(), visit));
        openMRSEncounter.setVisit(visit.getUuid());
//        story-todo - map audit observations
        var avniAuditObservations = (LinkedHashMap<String, Object>) subject.get("audit");
        return openMRSEncounter;
    }

    public OpenMRSEncounter mapSubjectToExistingEncounter(OpenMRSFullEncounter existingEncounter, Subject subject, String patientUuid, String encounterTypeUuid, Constants constants) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setUuid(existingEncounter.getUuid());
        openMRSEncounter.setEncounterDatetime(existingEncounter.getEncounterDatetime());
        openMRSEncounter.setPatient(existingEncounter.getPatient().getUuid());
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                (Map<String, Object>) subject.get("observations"),
                List.of(mappingMetaDataRepository.getBahmniValueForAvniIdConcept(),
                        mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEventDate_Concept)));
        openMRSEncounter.setObservations(existingGroupObs(existingEncounter, observations));
        return openMRSEncounter;
    }

    private List<OpenMRSSaveObservation> groupObs(List<OpenMRSSaveObservation> observations) {
        var formConcept = mappingMetaDataRepository.getMandatoryBahmniValue(MappingGroup.PatientSubject, MappingType.CommunityRegistration_BahmniForm);
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept);
        groupObservation.setGroupMembers(observations);
        return List.of(groupObservation);
    }

    private List<OpenMRSSaveObservation> existingGroupObs(OpenMRSFullEncounter existingEncounter, List<OpenMRSSaveObservation> observations) {
        var formConceptUuid = mappingMetaDataRepository.getMandatoryBahmniValue(MappingGroup.PatientSubject, MappingType.CommunityRegistration_BahmniForm);
        Optional<OpenMRSObservation> existingGroupObs = existingEncounter.findObservation(formConceptUuid);
        var groupObservation = new OpenMRSSaveObservation();
        existingGroupObs.ifPresent(o -> groupObservation.setUuid(o.getObsUuid()));
        groupObservation.setConcept(formConceptUuid);
        groupObservation.setGroupMembers(observations);
        return List.of(groupObservation);
    }

    private OpenMRSSaveObservation avniUuidObs(String avniEntityUuid) {
        var bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, avniEntityUuid, ObsDataType.Text);
    }

    private OpenMRSSaveObservation eventDateObs(Subject subject) {
        var bahmniValue = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEventDate_Concept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, FormatAndParseUtil.toISODateString(subject.getRegistrationDate()), ObsDataType.Date);
    }
}
