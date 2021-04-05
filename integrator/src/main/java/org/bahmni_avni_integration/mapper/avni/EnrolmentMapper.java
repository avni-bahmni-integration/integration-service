package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
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

    public OpenMRSEncounter mapEnrolmentToEncounter(Enrolment enrolment, String patientUuid, String encounterTypeUuid, Constants constants) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterDatetime(FormatAndParseUtil.toISODateStringWithTimezone(new Date()));
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));
        List<OpenMRSSaveObservation> observations = observationMapper.mapObservations((LinkedHashMap<String, Object>) enrolment.get("observations"));
        observations.add(avniUuidObs(enrolment));
        OpenMRSSaveObservation formGroupObservation = formGroupObservation(enrolment);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation formGroupObservation(Enrolment enrolment) {
        var formConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolment_BahmniForm, enrolment.getProgram());
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept);
        return groupObservation;
    }

    public OpenMRSEncounter mapEnrolmentToExistingEncounter(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        MappingMetaDataCollection encounterTypes = mappingMetaDataRepository.findAll(MappingGroup.ProgramEnrolment, MappingType.Community_Enrolment_EncounterType);
        String encounterTypeUuid = encounterTypes.getBahmniValueForAvniValue(enrolment.getProgram());

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setUuid(existingEncounter.getUuid());
        openMRSEncounter.setEncounterDatetime(existingEncounter.getEncounterDatetime());
        openMRSEncounter.setPatient(existingEncounter.getPatient().getUuid());
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        String avniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniUuidConcept();
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"),
                List.of(avniUuidConcept));
        OpenMRSSaveObservation formGroupObservation = formGroupObservation(enrolment);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation avniUuidObs(Enrolment enrolment) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniUuidConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, enrolment.getUuid(), ObsDataType.Text);
    }
}