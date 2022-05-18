package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.mapper.avni.EnrolmentMapper;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.bahmni.contract.OpenMRSEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;

import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrolmentService {
    private final MappingService mappingService;
    private final OpenMRSEncounterRepository openMRSEncounterRepository;
    private final EnrolmentMapper enrolmentMapper;
    private final ErrorService errorService;
    private final VisitService visitService;

    public EnrolmentService(MappingService mappingService, OpenMRSEncounterRepository openMRSEncounterRepository, EnrolmentMapper enrolmentMapper, ErrorService errorService, VisitService visitService) {
        this.mappingService = mappingService;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.enrolmentMapper = enrolmentMapper;
        this.errorService = errorService;
        this.visitService = visitService;
    }

    public OpenMRSFullEncounter findCommunityEnrolment(Enrolment enrolment, OpenMRSPatient patient) {
        return findCommunityEnrolment(enrolment, patient, BahmniMappingType.CommunityEnrolment_EncounterType);
    }

    public OpenMRSFullEncounter findCommunityExitEnrolment(Enrolment enrolment, OpenMRSPatient patient) {
        return findCommunityEnrolment(enrolment, patient, BahmniMappingType.CommunityEnrolmentExit_EncounterType);
    }

    private OpenMRSFullEncounter findCommunityEnrolment(Enrolment enrolment, OpenMRSPatient patient, MappingType mappingType) {
        String bahmniValueForAvniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        var encounterTypeUuid = mappingService.getBahmniValue(BahmniMappingGroup.ProgramEnrolment, mappingType, enrolment.getProgram());
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
