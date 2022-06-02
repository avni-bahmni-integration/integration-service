package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.ConstantKey;
import org.avni_integration_service.bahmni.contract.*;
import org.avni_integration_service.avni.domain.AvniBaseEncounter;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.ProgramEncounter;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EncounterMapper {
    private final MappingService mappingService;
    private final ObservationMapper observationMapper;
    private final BahmniMappingGroup bahmniMappingGroup;
    private final BahmniMappingType bahmniMappingType;

    public EncounterMapper(MappingService mappingService, ObservationMapper observationMapper,
                           BahmniMappingGroup bahmniMappingGroup, BahmniMappingType bahmniMappingType) {
        this.mappingService = mappingService;
        this.observationMapper = observationMapper;
        this.bahmniMappingGroup = bahmniMappingGroup;
        this.bahmniMappingType = bahmniMappingType;
    }

    public OpenMRSEncounter mapEncounter(GeneralEncounter generalEncounter, String patientUuid, Constants constants, OpenMRSVisit visit) {
        var encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.generalEncounter,
                bahmniMappingType.communityEncounterEncounterType,
                generalEncounter.getEncounterType());
        String formConceptUuid = mappingService.getBahmniFormUuidForGeneralEncounter(generalEncounter.getEncounterType());
        if (formConceptUuid == null) throw new RuntimeException(String.format("No form mapping setup for general encounter of type: %s", generalEncounter.getEncounterType()));
        return mapEncounter(generalEncounter, patientUuid, constants, visit, encounterTypeUuid, formConceptUuid);
    }

    public OpenMRSEncounter mapEncounter(ProgramEncounter programEncounter, String patientUuid, Constants constants, OpenMRSVisit visit) {
        var encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEncounter,
                bahmniMappingType.communityProgramEncounterEncounterType,
                programEncounter.getEncounterType());
        String formConceptUuid = mappingService.getBahmniFormUuidForProgramEncounter(programEncounter.getEncounterType());
        if (formConceptUuid == null) throw new RuntimeException(String.format("No form mapping setup for program encounter of type: %s", programEncounter.getEncounterType()));
        return mapEncounter(programEncounter, patientUuid, constants, visit, encounterTypeUuid, formConceptUuid);
    }

    private OpenMRSEncounter mapEncounter(AvniBaseEncounter baseEncounter, String patientUuid, Constants constants, OpenMRSVisit visit, String encounterTypeUuid, String formConceptUuid) {
        var openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatient(patientUuid);
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setEncounterDatetime(MapperUtils.getEventDateTime(baseEncounter.getEncounterDateTime(), visit));
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()),
                constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name())));

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
        String bahmniValueForAvniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, encounter.getUuid(), ObsDataType.Text);
    }

    private OpenMRSSaveObservation eventDateObs(AvniBaseEncounter encounter) {
        var bahmniValue = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniEventDateConcept);
        return OpenMRSSaveObservation.createPrimitiveObs(bahmniValue, FormatAndParseUtil.toISODateString(encounter.getEncounterDateTime()), ObsDataType.Date);
    }

    public OpenMRSEncounter mapEncounterToExistingEncounter(OpenMRSFullEncounter existingOpenMRSEncounter, ProgramEncounter programEncounter, Constants constants) {
        var encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEncounter,
                bahmniMappingType.communityProgramEncounterEncounterType,
                programEncounter.getEncounterType());
        String formConceptUuid = mappingService.getBahmniFormUuidForProgramEncounter(programEncounter.getEncounterType());
        return mapEncounterToExistingEncounter(existingOpenMRSEncounter, programEncounter, constants, encounterTypeUuid, formConceptUuid);
    }

    public OpenMRSEncounter mapEncounterToExistingEncounter(OpenMRSFullEncounter existingOpenMRSEncounter, GeneralEncounter generalEncounter, Constants constants) {
        var encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.generalEncounter,
                bahmniMappingType.communityEncounterEncounterType,
                generalEncounter.getEncounterType());
        String formConceptUuid = mappingService.getBahmniFormUuidForGeneralEncounter(generalEncounter.getEncounterType());
        return mapEncounterToExistingEncounter(existingOpenMRSEncounter, generalEncounter, constants, encounterTypeUuid, formConceptUuid);
    }

    private OpenMRSEncounter mapEncounterToExistingEncounter(OpenMRSFullEncounter existingEncounter, AvniBaseEncounter avniBaseEncounter, Constants constants, String encounterTypeUuid, String formConceptUuid) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setUuid(existingEncounter.getUuid());
        openMRSEncounter.setEncounterDatetime(existingEncounter.getEncounterDatetime());
        openMRSEncounter.setPatient(existingEncounter.getPatient().getUuid());
        openMRSEncounter.setEncounterType(encounterTypeUuid);
        openMRSEncounter.setLocation(constants.getValue(ConstantKey.IntegrationBahmniLocation.name()));
        openMRSEncounter.addEncounterProvider(new OpenMRSEncounterProvider(constants.getValue(ConstantKey.IntegrationBahmniProvider.name()), constants.getValue(ConstantKey.IntegrationBahmniEncounterRole.name())));

        String avniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        String eventDateConcept = mappingService.getBahmniValue(bahmniMappingGroup.common, bahmniMappingType.avniEventDateConcept);
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
