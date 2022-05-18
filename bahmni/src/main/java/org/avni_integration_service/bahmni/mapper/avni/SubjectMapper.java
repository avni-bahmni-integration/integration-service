package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.ConstantKey;
import org.avni_integration_service.bahmni.contract.*;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SubjectMapper {
    private final MappingService mappingService;
    private final ObservationMapper observationMapper;

    public SubjectMapper(MappingService mappingService, ObservationMapper observationMapper) {
        this.mappingService = mappingService;
        this.observationMapper = observationMapper;
    }

    public OpenMRSEncounter mapSubjectToEncounter(Subject subject, String patientUuid, String encounterTypeUuid, Constants constants, OpenMRSVisit visit) {
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));

        var encounterProvider = new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name()));
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
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name())));

        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                (Map<String, Object>) subject.get("observations"),
                List.of(mappingService.getBahmniValueForAvniIdConcept(),
                        mappingService.getBahmniValue(MappingGroup.Common, BahmniMappingType.AvniEventDate_Concept)));
        openMRSEncounter.setObservations(existingGroupObs(existingEncounter, observations));
        return openMRSEncounter;
    }

    private List<OpenMRSSaveObservation> groupObs(List<OpenMRSSaveObservation> observations) {
        var formConcept = mappingService.getBahmniValue(BahmniMappingGroup.PatientSubject, BahmniMappingType.CommunityRegistration_BahmniForm);
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept);
        groupObservation.setGroupMembers(observations);
        return List.of(groupObservation);
    }

    private List<OpenMRSSaveObservation> existingGroupObs(OpenMRSFullEncounter existingEncounter, List<OpenMRSSaveObservation> observations) {
        var formConceptUuid = mappingService.getBahmniValue(BahmniMappingGroup.PatientSubject, BahmniMappingType.CommunityRegistration_BahmniForm);
        Optional<OpenMRSObservation> existingGroupObs = existingEncounter.findObservation(formConceptUuid);
        var groupObservation = new OpenMRSSaveObservation();
        existingGroupObs.ifPresent(o -> groupObservation.setUuid(o.getObsUuid()));
        groupObservation.setConcept(formConceptUuid);
        groupObservation.setGroupMembers(observations);
        return List.of(groupObservation);
    }

    private OpenMRSSaveObservation avniUuidObs(String avniEntityUuid) {
        var bahmniValueForAvniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, avniEntityUuid, ObsDataType.Text);
    }

    private OpenMRSSaveObservation eventDateObs(Subject subject) {
        var bahmniValue = mappingService.getBahmniValue(MappingGroup.Common, BahmniMappingType.AvniEventDate_Concept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, FormatAndParseUtil.toISODateString(subject.getRegistrationDate()), ObsDataType.Date);
    }
}
