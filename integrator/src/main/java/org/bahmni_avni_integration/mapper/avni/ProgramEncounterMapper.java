package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
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

    public OpenMRSEncounter mapEncounter(ProgramEncounter programEncounter, String patientUuid, Constants constants, OpenMRSVisit visit) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_EncounterType,
                programEncounter.getEncounterType());
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setEncounterDatetime(MapperUtils.getEventDateTime(programEncounter.getEncounterDateTime(), visit));
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        List<OpenMRSSaveObservation> observations = observationMapper.mapObservations((LinkedHashMap<String, Object>) programEncounter.get("observations"));
        observations.add(avniUuidObs(programEncounter));
        observations.add(eventDateObs(programEncounter));
        OpenMRSSaveObservation formGroupObservation = formGroupObservation(programEncounter);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        openMRSEncounter.setVisit(visit.getUuid());
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation formGroupObservation(ProgramEncounter programEncounter) {
        var groupObservation = new OpenMRSSaveObservation();
        String concept = formConcept(programEncounter);
        if (concept == null) throw new RuntimeException(String.format("No form mapping setup for program encounter of type: %s", programEncounter.getEncounterType()));
        groupObservation.setConcept(concept);
        return groupObservation;
    }

    private String formConcept(ProgramEncounter programEncounter) {
        return mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_BahmniForm,
                programEncounter.getEncounterType());
    }

    private OpenMRSSaveObservation avniUuidObs(ProgramEncounter programEncounter) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, programEncounter.getUuid(), ObsDataType.Text);
    }

    private OpenMRSSaveObservation eventDateObs(ProgramEncounter programEncounter) {
        var bahmniValue = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEventDate_Concept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, FormatAndParseUtil.toISODateString(programEncounter.getEncounterDateTime()), ObsDataType.Date);
    }

    public OpenMRSEncounter mapProgramEncounterToExistingEncounter(OpenMRSFullEncounter existingEncounter, ProgramEncounter programEncounter, Constants constants) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_EncounterType,
                programEncounter.getEncounterType());

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setUuid(existingEncounter.getUuid());
        openMRSEncounter.setEncounterDatetime(existingEncounter.getEncounterDatetime());
        openMRSEncounter.setPatient(existingEncounter.getPatient().getUuid());
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        String avniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        String eventDateConcept = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEventDate_Concept);
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                (Map<String, Object>) programEncounter.get("observations"),
                List.of(avniUuidConcept, eventDateConcept));
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
