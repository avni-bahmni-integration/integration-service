package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.mapper.avni.EnrolmentMapper;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

@Service
public class EnrolmentService {

    private final PatientService patientService;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final OpenMRSEncounterRepository openMRSEncounterRepository;
    private final EnrolmentMapper enrolmentMapper;
    private final ErrorService errorService;

    public EnrolmentService(PatientService patientService, MappingMetaDataRepository mappingMetaDataRepository, OpenMRSEncounterRepository openMRSEncounterRepository, EnrolmentMapper enrolmentMapper, ErrorService errorService) {
        this.patientService = patientService;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.enrolmentMapper = enrolmentMapper;
        this.errorService = errorService;
    }

    public Pair<OpenMRSUuidHolder, OpenMRSFullEncounter> findCommunityEnrolment(Enrolment enrolment, Subject subject, Constants constants, SubjectToPatientMetaData subjectToPatientMetaData) {
        OpenMRSUuidHolder patient = patientService.findPatient(subject, constants, subjectToPatientMetaData);
        if (patient == null) {
            return new Pair<>(null, null);
        }
        String enrolmentConceptUuid = mappingMetaDataRepository
                .getBahmniValue(MappingGroup.ProgramEnrolment, MappingType.EnrolmentUUID_Concept);
        OpenMRSFullEncounter encounter = openMRSEncounterRepository
                .getEncounterByPatientAndObservation(patient.getUuid(), enrolmentConceptUuid, enrolment.getUuid());
        return new Pair<>(patient, encounter);
    }

    public void processPatientNotFound(Subject subject, SubjectToPatientMetaData metaData) {
        errorService.errorOccurred(subject, ErrorType.NoPatientWithId, metaData);
    }

    public OpenMRSFullEncounter createCommunityEnrolment(Enrolment enrolment, OpenMRSUuidHolder openMRSPatient, Constants constants) {
        MappingMetaDataCollection encounterTypes = mappingMetaDataRepository.findAll(MappingGroup.ProgramEnrolment, MappingType.Community_Enrolment_EncounterType);
        String encounterTypeUuid = encounterTypes.getBahmniValueForAvniValue(enrolment.getProgram());
        OpenMRSEncounter encounter = enrolmentMapper.mapEnrolmentToEncounter(enrolment, openMRSPatient.getUuid(), encounterTypeUuid, constants);
        OpenMRSFullEncounter savedEncounter = openMRSEncounterRepository.createEncounter(encounter);

        errorService.successfullyProcessed(enrolment);
        return savedEncounter;
    }

    public void updateCommunityEnrolment(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        OpenMRSEncounter openMRSEncounter = enrolmentMapper.mapEnrolmentToExistingEncounter(existingEncounter, enrolment, constants);
        openMRSEncounterRepository.updateEncounter(openMRSEncounter);
        errorService.successfullyProcessed(enrolment);
    }
}