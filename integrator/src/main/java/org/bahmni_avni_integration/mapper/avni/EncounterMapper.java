package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.AvniBaseEncounter;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EncounterMapper {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ObservationMapper observationMapper;

    public EncounterMapper(MappingMetaDataRepository mappingMetaDataRepository, ObservationMapper observationMapper) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.observationMapper = observationMapper;
    }

    public OpenMRSEncounter mapEncounter(GeneralEncounter generalEncounter, String patientUuid, Constants constants, OpenMRSVisit visit) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.GeneralEncounter,
                MappingType.CommunityEncounter_EncounterType,
                generalEncounter.getEncounterType());
        String formConceptUuid = mappingMetaDataRepository.getBahmniFormUuidForGeneralEncounter(generalEncounter.getEncounterType());
        if (formConceptUuid == null) throw new RuntimeException(String.format("No form mapping setup for general encounter of type: %s", generalEncounter.getEncounterType()));
        return mapEncounter(generalEncounter, patientUuid, constants, visit, encounterTypeUuid, formConceptUuid);
    }

    public OpenMRSEncounter mapEncounter(ProgramEncounter programEncounter, String patientUuid, Constants constants, OpenMRSVisit visit) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_EncounterType,
                programEncounter.getEncounterType());
        String formConceptUuid = mappingMetaDataRepository.getBahmniFormUuidForGeneralEncounter(programEncounter.getEncounterType());
        if (formConceptUuid == null) throw new RuntimeException(String.format("No form mapping setup for program encounter of type: %s", programEncounter.getEncounterType()));
        return mapEncounter(programEncounter, patientUuid, constants, visit, encounterTypeUuid, formConceptUuid);
    }

    private OpenMRSEncounter mapEncounter(AvniBaseEncounter baseEncounter, String patientUuid, Constants constants, OpenMRSVisit visit, String encounterTypeUuid, String formConceptUuid) {
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setEncounterDatetime(MapperUtils.getEventDateTime(baseEncounter.getEncounterDateTime(), visit));
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole)));

        List<OpenMRSSaveObservation> observations = observationMapper.mapObservations((LinkedHashMap<String, Object>) baseEncounter.get("observations"));
        observations.add(avniUuidObs(baseEncounter));
        observations.add(eventDateObs(baseEncounter));
        OpenMRSSaveObservation formGroupObservation = formGroupObservation(formConceptUuid);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        openMRSEncounter.setVisit(visit.getUuid());
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation formGroupObservation(String formConceptUuid) {
        var groupObservation = new OpenMRSSaveObservation();
        groupObservation.setConcept(formConceptUuid);
        return groupObservation;
    }

    private OpenMRSSaveObservation avniUuidObs(AvniBaseEncounter encounter) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, encounter.getUuid(), ObsDataType.Text);
    }

    private OpenMRSSaveObservation eventDateObs(AvniBaseEncounter encounter) {
        var bahmniValue = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common, MappingType.AvniEventDate_Concept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, FormatAndParseUtil.toISODateString(encounter.getEncounterDateTime()), ObsDataType.Date);
    }

    public OpenMRSEncounter mapEncounterToExistingEncounter(OpenMRSFullEncounter existingOpenMRSEncounter, ProgramEncounter programEncounter, Constants constants) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_EncounterType,
                programEncounter.getEncounterType());
        String formConceptUuid = mappingMetaDataRepository.getBahmniFormUuidForProgramEncounter(programEncounter.getEncounterType());
        return mapEncounterToExistingEncounter(existingOpenMRSEncounter, programEncounter, constants, encounterTypeUuid, formConceptUuid);
    }

    public OpenMRSEncounter mapEncounterToExistingEncounter(OpenMRSFullEncounter existingOpenMRSEncounter, GeneralEncounter generalEncounter, Constants constants) {
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.GeneralEncounter,
                MappingType.CommunityEncounter_EncounterType,
                generalEncounter.getEncounterType());
        String formConceptUuid = mappingMetaDataRepository.getBahmniFormUuidForGeneralEncounter(generalEncounter.getEncounterType());
        return mapEncounterToExistingEncounter(existingOpenMRSEncounter, generalEncounter, constants, encounterTypeUuid, formConceptUuid);
    }

    private OpenMRSEncounter mapEncounterToExistingEncounter(OpenMRSFullEncounter existingEncounter, AvniBaseEncounter avniBaseEncounter, Constants constants, String encounterTypeUuid, String formConceptUuid) {
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
                (Map<String, Object>) avniBaseEncounter.get("observations"),
                List.of(avniUuidConcept, eventDateConcept));
        OpenMRSSaveObservation formGroupObservation = existingGroupObs(existingEncounter, formConceptUuid);
        formGroupObservation.setGroupMembers(observations);
        openMRSEncounter.setObservations(List.of(formGroupObservation));
        return openMRSEncounter;
    }

    private OpenMRSSaveObservation existingGroupObs(OpenMRSFullEncounter existingEncounter, String formConceptUuid) {
        Optional<OpenMRSObservation> existingGroupObs = existingEncounter.findObservation(formConceptUuid);
        var groupObservation = new OpenMRSSaveObservation();
        existingGroupObs.ifPresent(o -> groupObservation.setUuid(o.getObsUuid()));
        groupObservation.setConcept(formConceptUuid);
        return groupObservation;
    }
}
