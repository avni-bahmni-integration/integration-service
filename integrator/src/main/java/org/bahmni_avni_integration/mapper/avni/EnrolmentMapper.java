package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EnrolmentMapper {

    private final ObservationMapper observationMapper;
    private final MappingMetaDataRepository mappingMetaDataRepository;

    public EnrolmentMapper(ObservationMapper observationMapper, MappingMetaDataRepository mappingMetaDataRepository) {
        this.observationMapper = observationMapper;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public OpenMRSEncounter mapEnrolmentToEnrolmentEncounter(Enrolment enrolment, String patientUuid, OpenMRSVisit visit, Constants constants) {
        var encounterTypes = mappingMetaDataRepository.findAll(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolment_EncounterType);
        var encounterTypeUuid = encounterTypes.getBahmniValueForAvniValue(enrolment.getProgram());
        var formGroupObservation = formGroupObservation(enrolment, MappingType.CommunityEnrolment_BahmniForm);
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
        var encounterTypes = mappingMetaDataRepository.findAll(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolmentExit_EncounterType);
        var encounterTypeUuid = encounterTypes.getBahmniValueForAvniValue(enrolment.getProgram());
        var formGroupObservation = formGroupObservation(enrolment, MappingType.CommunityEnrolmentExit_BahmniForm);
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
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));
        List<OpenMRSSaveObservation> observations = observationMapper.mapObservations(avniObservations);
        observations.add(avniUuidObs(enrolment.getUuid()));
        observations.add(enrolmentDateObs(enrolment));
        observations.add(programDataObs(enrolment, isExit));
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private String getEncounterDateTime(Enrolment enrolment, OpenMRSVisit visit, boolean isExit) {
        var encounterDateTime = isExit ? enrolment.getExitDateTime() : enrolment.getEnrolmentDateTime();
        return MapperUtils.getEntityDateTime(encounterDateTime, visit);
    }


    private OpenMRSSaveObservation formGroupObservation(Enrolment enrolment, MappingType mappingType) {
        var formConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, mappingType, enrolment.getProgram());
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept);
        return groupObservation;
    }

    private OpenMRSSaveObservation existingGroupObs(Enrolment enrolment, MappingType mappingType, OpenMRSFullEncounter existingEncounter) {
        var formConceptUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, mappingType, enrolment.getProgram());
        Optional<OpenMRSObservation> existingGroupObs = existingEncounter.findObservation(formConceptUuid);
        var groupObservation = new OpenMRSSaveObservation();
        existingGroupObs.ifPresent(o -> groupObservation.setUuid(o.getObsUuid()));
        groupObservation.setConcept(formConceptUuid);
        return groupObservation;
    }

    public OpenMRSEncounter mapEnrolmentToExistingEnrolmentEncounter(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        String encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment,
                MappingType.CommunityEnrolment_EncounterType,
                enrolment.getProgram());
        OpenMRSSaveObservation formGroupObservation = existingGroupObs(enrolment, MappingType.CommunityEnrolment_BahmniForm, existingEncounter);
        return mapEnrolmentToExistingEncounter(existingEncounter,
                (Map<String, Object>) enrolment.get("observations"),
                formGroupObservation,
                encounterTypeUuid,
                constants);
    }

    public OpenMRSEncounter mapEnrolmentToExistingEnrolmentExitEncounter(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        String encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment,
                MappingType.CommunityEnrolmentExit_EncounterType,
                enrolment.getProgram());
        OpenMRSSaveObservation formGroupObservation = existingGroupObs(enrolment, MappingType.CommunityEnrolmentExit_BahmniForm, existingEncounter);
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
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        String avniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        String enrolmentDateConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEnrolmentDate_Concept);
        String programDataConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniProgramData_Concept);
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                avniObservations,
                List.of(avniUuidConcept, enrolmentDateConcept, programDataConcept));
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation avniUuidObs(String enrolmentUuid) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, enrolmentUuid, ObsDataType.Text);
    }

    private OpenMRSSaveObservation enrolmentDateObs(Enrolment enrolment) {
        var bahmniValue = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEnrolmentDate_Concept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, enrolment.get("Enrolment datetime"), ObsDataType.Date);
    }

    private OpenMRSSaveObservation programDataObs(Enrolment enrolment, boolean isExit) {
        var bahmniValue = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniProgramData_Concept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue,
                String.format("%s - %s - %s",
                        enrolment.getProgram(),
                        isExit ? "Exit" : "Enrolment",
                        isExit ? FormatAndParseUtil.toHumanReadableFormat(enrolment.getExitDateTime()) : FormatAndParseUtil.toHumanReadableFormat(enrolment.getEnrolmentDateTime())),
                ObsDataType.Text);
    }
}