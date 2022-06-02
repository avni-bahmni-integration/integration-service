package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.ConstantKey;
import org.avni_integration_service.bahmni.contract.*;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EnrolmentMapper {

    private final ObservationMapper observationMapper;
    private final MappingService mappingService;
    private final BahmniMappingGroup bahmniMappingGroup;
    private final BahmniMappingType bahmniMappingType;

    @Autowired
    public EnrolmentMapper(MappingService mappingService, ObservationMapper observationMapper,
                           BahmniMappingGroup bahmniMappingGroup, BahmniMappingType bahmniMappingType) {
        this.mappingService = mappingService;
        this.observationMapper = observationMapper;
        this.bahmniMappingGroup = bahmniMappingGroup;
        this.bahmniMappingType = bahmniMappingType;
    }

    public OpenMRSEncounter mapEnrolmentToEnrolmentEncounter(Enrolment enrolment, String patientUuid, OpenMRSVisit visit, Constants constants) {
        var encounterTypes = mappingService.findAll(bahmniMappingGroup.programEnrolment, bahmniMappingType.communityEnrolmentEncounterType);
        var encounterTypeUuid = encounterTypes.getBahmniValueForAvniValue(enrolment.getProgram());
        var formGroupObservation = formGroupObservation(enrolment, bahmniMappingType.communityEnrolmentBahmniForm);
        return mapEnrolmentToEncounter(enrolment,
                (LinkedHashMap<String, Object>) enrolment.get("observations"),
                formGroupObservation,
                patientUuid,
                encounterTypeUuid,
                constants,
                visit,
                false);
    }

    public OpenMRSEncounter mapEnrolmentToExitEncounter(Enrolment enrolment, String patientUuid, OpenMRSVisit visit, Constants constants) {
        var encounterTypes = mappingService.findAll(bahmniMappingGroup.programEnrolment, bahmniMappingType.communityEnrolmentExitEncounterType);
        var encounterTypeUuid = encounterTypes.getBahmniValueForAvniValue(enrolment.getProgram());
        var formGroupObservation = formGroupObservation(enrolment, bahmniMappingType.communityEnrolmentExitBahmniForm);
        return mapEnrolmentToEncounter(enrolment,
                (LinkedHashMap<String, Object>) enrolment.get("exitObservations"),
                formGroupObservation,
                patientUuid,
                encounterTypeUuid,
                constants,
                visit,
                true);
    }

    private OpenMRSEncounter mapEnrolmentToEncounter(Enrolment enrolment,
                                                     LinkedHashMap<String, Object> avniObservations,
                                                     OpenMRSSaveObservation formGroupObservation,
                                                     String patientUuid,
                                                     String encounterTypeUuid,
                                                     Constants constants,
                                                     OpenMRSVisit visit,
                                                     boolean isExit) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setEncounterDatetime(getEncounterDateTime(enrolment, visit, isExit));
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name())));
        List<OpenMRSSaveObservation> observations = observationMapper.mapObservations(avniObservations);
        observations.add(avniUuidObs(enrolment.getUuid()));
        observations.add(eventDateObs(enrolment, isExit));
        observations.add(programDataObs(enrolment, isExit));
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private String getEncounterDateTime(Enrolment enrolment, OpenMRSVisit visit, boolean isExit) {
        return MapperUtils.getEventDateTime(isExit ? enrolment.getExitDateTime() : enrolment.getEnrolmentDateTime(), visit);
    }


    private OpenMRSSaveObservation formGroupObservation(Enrolment enrolment, MappingType mappingType) {
        var formConcept = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment, mappingType, enrolment.getProgram());
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept);
        return groupObservation;
    }

    private OpenMRSSaveObservation existingGroupObs(Enrolment enrolment, MappingType mappingType, OpenMRSFullEncounter existingEncounter) {
        var formConceptUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment, mappingType, enrolment.getProgram());
        Optional<OpenMRSObservation> existingGroupObs = existingEncounter.findObservation(formConceptUuid);
        var groupObservation = new OpenMRSSaveObservation();
        existingGroupObs.ifPresent(o -> groupObservation.setUuid(o.getObsUuid()));
        groupObservation.setConcept(formConceptUuid);
        return groupObservation;
    }

    public OpenMRSEncounter mapEnrolmentToExistingEnrolmentEncounter(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        String encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment,
                bahmniMappingType.communityEnrolmentEncounterType,
                enrolment.getProgram());
        OpenMRSSaveObservation formGroupObservation = existingGroupObs(enrolment, bahmniMappingType.communityEnrolmentBahmniForm, existingEncounter);
        return mapEnrolmentToExistingEncounter(existingEncounter,
                (Map<String, Object>) enrolment.get("observations"),
                formGroupObservation,
                encounterTypeUuid,
                constants);
    }

    public OpenMRSEncounter mapEnrolmentToExistingEnrolmentExitEncounter(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        String encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment,
                bahmniMappingType.communityEnrolmentExitEncounterType,
                enrolment.getProgram());
        OpenMRSSaveObservation formGroupObservation = existingGroupObs(enrolment, bahmniMappingType.communityEnrolmentExitBahmniForm, existingEncounter);
        return mapEnrolmentToExistingEncounter(existingEncounter,
                (Map<String, Object>) enrolment.get("exitObservations"),
                formGroupObservation,
                encounterTypeUuid,
                constants);
    }

    private OpenMRSEncounter mapEnrolmentToExistingEncounter(OpenMRSFullEncounter existingEncounter,
                                                             Map<String, Object> avniObservations,
                                                             OpenMRSSaveObservation formGroupObservation,
                                                             String encounterTypeUuid,
                                                             Constants constants) {

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setUuid(existingEncounter.getUuid());
        openMRSEncounter.setEncounterDatetime(existingEncounter.getEncounterDatetime());
        openMRSEncounter.setPatient(existingEncounter.getPatient().getUuid());
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name())));

        String avniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        String eventDateConcept = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniEventDateConcept);
        String programDataConcept = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniProgramDataConcept);
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                avniObservations,
                List.of(avniUuidConcept, eventDateConcept, programDataConcept));
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation avniUuidObs(String enrolmentUuid) {
        String bahmniValueForAvniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, enrolmentUuid, ObsDataType.Text);
    }

    private OpenMRSSaveObservation eventDateObs(Enrolment enrolment, boolean isExit) {
        var obsConcept = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniEventDateConcept);
        var obsValue = FormatAndParseUtil.toISODateString(isExit ? enrolment.getExitDateTime() : enrolment.getEnrolmentDateTime());
        return OpenMRSSaveObservation.createPrimitiveObs(obsConcept, obsValue, ObsDataType.Date);
    }

    private OpenMRSSaveObservation programDataObs(Enrolment enrolment, boolean isExit) {
        var bahmniValue = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniProgramDataConcept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue,
                String.format("%s - %s - %s",
                        enrolment.getProgram(),
                        isExit ? "Exit" : "Enrolment",
                        isExit ? FormatAndParseUtil.toHumanReadableFormat(enrolment.getExitDateTime()) : FormatAndParseUtil.toHumanReadableFormat(enrolment.getEnrolmentDateTime())),
                ObsDataType.Text);
    }
}
