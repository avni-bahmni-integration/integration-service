package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounterProvider;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveObservation;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
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

        mapEnrolmentUuid(enrolment, openMRSEncounter);
        observationMapper.mapObservations((LinkedHashMap<String, Object>) enrolment.get("observations"), openMRSEncounter);
        return openMRSEncounter;
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

        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniUuidConcept();
        var observations = observationMapper.updateOpenMRSObservationsFromAvniObservations(
                existingEncounter.getLeafObservations(),
                (Map<String, Object>) enrolment.get("observations"),
                List.of(bahmniValueForAvniUuidConcept));
        openMRSEncounter.setObservations(observations);
        return openMRSEncounter;
    }

    private void mapEnrolmentUuid(Enrolment enrolment, OpenMRSEncounter openMRSEncounter) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniUuidConcept();
        openMRSEncounter.addObservation(OpenMRSSaveObservation.createPrimitiveObs(bahmniValueForAvniUuidConcept, enrolment.getUuid(), ObsDataType.Text));
    }
}