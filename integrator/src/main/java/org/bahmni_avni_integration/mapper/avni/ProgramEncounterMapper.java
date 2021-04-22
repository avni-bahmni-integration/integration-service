package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProgramEncounterMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private ObservationMapper observationMapper;

    public ProgramEncounterMapper(MappingMetaDataRepository mappingMetaDataRepository, ObservationMapper observationMapper) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.observationMapper = observationMapper;
    }

    public OpenMRSEncounter mapEncounter(ProgramEncounter programEncounter, String patientUuid, Constants constants) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_EncounterType,
                avniValueForEncounterType(programEncounter.getProgram(), programEncounter.getEncounterType()));
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        List<OpenMRSSaveObservation> observations = observationMapper.mapObservations((LinkedHashMap<String, Object>) programEncounter.get("observations"));
        observations.add(avniUuidObs(programEncounter));
        OpenMRSSaveObservation formGroupObservation = formGroupObservation(programEncounter);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation formGroupObservation(ProgramEncounter programEncounter) {
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConcept(programEncounter));
        return groupObservation;
    }

    private String formConcept(ProgramEncounter programEncounter) {
        return mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                    MappingType.CommunityProgramEncounter_BahmniForm,
                    avniValueForEncounterType(programEncounter.getProgram(), programEncounter.getEncounterType()));
    }

    private OpenMRSSaveObservation avniUuidObs(ProgramEncounter programEncounter) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, programEncounter.getUuid(), ObsDataType.Text);
    }

    private String avniValueForEncounterType(String programName, String encounterTypeName) {
        return String.format("%s-%s", programName, encounterTypeName);
    }

    public OpenMRSEncounter mapProgramEncounterToExistingEncounter(OpenMRSFullEncounter existingEncounter, ProgramEncounter programEncounter, Constants constants) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_EncounterType,
                avniValueForEncounterType(programEncounter.getProgram(), programEncounter.getEncounterType()));

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setUuid(existingEncounter.getUuid());
        openMRSEncounter.setEncounterDatetime(existingEncounter.getEncounterDatetime());
        openMRSEncounter.setPatient(existingEncounter.getPatient().getUuid());
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        String avniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                (Map<String, Object>) programEncounter.get("observations"),
                List.of(avniUuidConcept));
        OpenMRSSaveObservation formGroupObservation = existingGroupObs(programEncounter, existingEncounter);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation existingGroupObs(ProgramEncounter programEncounter, OpenMRSFullEncounter existingEncounter) {
        var formConceptUuid = formConcept(programEncounter);
        Optional<OpenMRSObservation> existingGroupObs = existingEncounter.findObservation(formConceptUuid);
        var groupObservation = new OpenMRSSaveObservation();
        existingGroupObs.ifPresent(o -> groupObservation.setUuid(o.getObsUuid()));
        groupObservation.setConcept(formConceptUuid);
        return groupObservation;
    }
}