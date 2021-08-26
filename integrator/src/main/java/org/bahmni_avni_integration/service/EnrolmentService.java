package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.mapper.avni.EnrolmentMapper;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrolmentService {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final OpenMRSEncounterRepository openMRSEncounterRepository;
    private final EnrolmentMapper enrolmentMapper;
    private final ErrorService errorService;
    private final VisitService visitService;

    public EnrolmentService(MappingMetaDataRepository mappingMetaDataRepository, OpenMRSEncounterRepository openMRSEncounterRepository, EnrolmentMapper enrolmentMapper, ErrorService errorService, VisitService visitService) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.enrolmentMapper = enrolmentMapper;
        this.errorService = errorService;
        this.visitService = visitService;
    }

    public OpenMRSFullEncounter findCommunityEnrolment(Enrolment enrolment, OpenMRSPatient patient) {
        return findCommunityEnrolment(enrolment, patient, MappingType.CommunityEnrolment_EncounterType);
    }

    public OpenMRSFullEncounter findCommunityExitEnrolment(Enrolment enrolment, OpenMRSPatient patient) {
        return findCommunityEnrolment(enrolment, patient, MappingType.CommunityEnrolmentExit_EncounterType);
    }

    private OpenMRSFullEncounter findCommunityEnrolment(Enrolment enrolment, OpenMRSPatient patient, MappingType mappingType) {
        String bahmniValueForAvniUuidConcept = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();
        var encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, mappingType, enrolment.getProgram());
        OpenMRSFullEncounter encounter = openMRSEncounterRepository
                .getEncounterByPatientAndObservationAndEncType(patient.getUuid(), bahmniValueForAvniUuidConcept, enrolment.getUuid(), encounterTypeUuid);
        return encounter;
    }

    public void processPatientNotFound(Enrolment enrolment) {
        errorService.errorOccurred(enrolment, ErrorType.NoPatientWithId);
    }

    public OpenMRSFullEncounter createCommunityEnrolment(Enrolment enrolment, OpenMRSPatient openMRSPatient, Constants constants) {
        if (enrolment.getVoided()) return null;
        var visit = visitService.getOrCreateVisit(openMRSPatient, enrolment);
        var encounter = enrolmentMapper.mapEnrolmentToEnrolmentEncounter(enrolment, openMRSPatient.getUuid(), visit, constants);
        encounter.setVisit(visit.getUuid());
        var savedEncounter = openMRSEncounterRepository.createEncounter(encounter);
        return savedEncounter;
    }

    public OpenMRSFullEncounter createCommunityExitEnrolment(Enrolment enrolment, OpenMRSPatient openMRSPatient, Constants constants) {
        if (enrolment.getVoided()) return null;
        var visit = visitService.getOrCreateVisit(openMRSPatient, enrolment);
        var encounter = enrolmentMapper.mapEnrolmentToExitEncounter(enrolment, openMRSPatient.getUuid(), visit, constants);
        encounter.setVisit(visit.getUuid());
        var savedEncounter = openMRSEncounterRepository.createEncounter(encounter);
        return savedEncounter;
    }

    public void updateCommunityEnrolment(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        if (enrolment.getVoided()) {
            openMRSEncounterRepository.voidEncounter(existingEncounter);
            visitService.voidVisit(enrolment, existingEncounter);
        } else {
            OpenMRSEncounter openMRSEncounter = enrolmentMapper.mapEnrolmentToExistingEnrolmentEncounter(existingEncounter, enrolment, constants);
            openMRSEncounterRepository.updateEncounter(openMRSEncounter);
        }
    }

    public void updateCommunityExitEnrolment(OpenMRSFullEncounter existingEncounter, Enrolment enrolment, Constants constants) {
        if (enrolment.getVoided()) {
            openMRSEncounterRepository.voidEncounter(existingEncounter);
        } else {
            OpenMRSEncounter openMRSEncounter = enrolmentMapper.mapEnrolmentToExistingEnrolmentExitEncounter(existingEncounter, enrolment, constants);
            openMRSEncounterRepository.updateEncounter(openMRSEncounter);
        }
    }
}
