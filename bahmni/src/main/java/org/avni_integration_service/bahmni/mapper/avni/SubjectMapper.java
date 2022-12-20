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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SubjectMapper {
    private final MappingService mappingService;
    private final ObservationMapper observationMapper;
    private final BahmniMappingGroup bahmniMappingGroup;
    private final BahmniMappingType bahmniMappingType;
    @Autowired
    public SubjectMapper(MappingService mappingService, ObservationMapper observationMapper,
                           BahmniMappingGroup bahmniMappingGroup, BahmniMappingType bahmniMappingType) {
        this.mappingService = mappingService;
        this.observationMapper = observationMapper;
        this.bahmniMappingGroup = bahmniMappingGroup;
        this.bahmniMappingType = bahmniMappingType;
    }

    public OpenMRSEncounter mapSubjectToEncounter(Subject subject, String patientUuid, String encounterTypeUuid, Constants constants, OpenMRSVisit visit) {
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));

        var encounterProvider = new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name()));
        openMRSEncounter.addEncounterProvider(encounterProvider);

        var observations = observationMapper.mapObservations(subject.getObservations());
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
                subject.getObservations(),
                List.of(mappingService.getBahmniValueForAvniIdConcept(),
                        mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniEventDateConcept)));
        openMRSEncounter.setObservations(existingGroupObs(existingEncounter, observations));
        return openMRSEncounter;
    }

    private List<OpenMRSSaveObservation> groupObs(List<OpenMRSSaveObservation> observations) {
        var formConcept = mappingService.getBahmniValue(bahmniMappingGroup.patientSubject, bahmniMappingType.communityRegistrationBahmniForm);
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept);
        groupObservation.setGroupMembers(observations);
        return List.of(groupObservation);
    }

    private List<OpenMRSSaveObservation> existingGroupObs(OpenMRSFullEncounter existingEncounter, List<OpenMRSSaveObservation> observations) {
        var formConceptUuid = mappingService.getBahmniValue(bahmniMappingGroup.patientSubject, bahmniMappingType.communityRegistrationBahmniForm);
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
        var bahmniValue = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniEventDateConcept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, FormatAndParseUtil.toISODateString(subject.getRegistrationDate()), ObsDataType.Date);
    }
}
