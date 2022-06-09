package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.bahmni.BahmniErrorType;
import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.contract.OpenMRSEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.bahmni.mapper.avni.EnrolmentMapper;
import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.springframework.stereotype.Service;

@Service
public class EnrolmentService {
    private final MappingService mappingService;
    private final OpenMRSEncounterRepository openMRSEncounterRepository;
    private final EnrolmentMapper enrolmentMapper;
    private final AvniBahmniErrorService avniBahmniErrorService;
    private final VisitService visitService;
    private final BahmniMappingGroup bahmniMappingGroup;
    private final BahmniMappingType bahmniMappingType;

    public EnrolmentService(MappingService mappingService, OpenMRSEncounterRepository openMRSEncounterRepository,
                            EnrolmentMapper enrolmentMapper, AvniBahmniErrorService avniBahmniErrorService, VisitService visitService,
                            BahmniMappingGroup bahmniMappingGroup, BahmniMappingType bahmniMappingType) {
        this.mappingService = mappingService;
        this.openMRSEncounterRepository = openMRSEncounterRepository;
        this.enrolmentMapper = enrolmentMapper;
        this.avniBahmniErrorService = avniBahmniErrorService;
        this.visitService = visitService;
        this.bahmniMappingGroup = bahmniMappingGroup;
        this.bahmniMappingType = bahmniMappingType;
    }

    public OpenMRSFullEncounter findCommunityEnrolment(Enrolment enrolment, OpenMRSPatient patient) {
        return findCommunityEnrolment(enrolment, patient, bahmniMappingType.communityEnrolmentEncounterType);
    }

    public OpenMRSFullEncounter findCommunityExitEnrolment(Enrolment enrolment, OpenMRSPatient patient) {
        return findCommunityEnrolment(enrolment, patient, bahmniMappingType.communityEnrolmentExitEncounterType);
    }

    private OpenMRSFullEncounter findCommunityEnrolment(Enrolment enrolment, OpenMRSPatient patient, MappingType mappingType) {
        String bahmniValueForAvniUuidConcept = mappingService.getBahmniValueForAvniIdConcept();
        var encounterTypeUuid = mappingService.getBahmniValue(bahmniMappingGroup.programEnrolment, mappingType, enrolment.getProgram());
        OpenMRSFullEncounter encounter = openMRSEncounterRepository
                .getEncounterByPatientAndObservationAndEncType(patient.getUuid(), bahmniValueForAvniUuidConcept, enrolment.getUuid(), encounterTypeUuid);
        return encounter;
    }

    public void processPatientNotFound(Enrolment enrolment) {
        avniBahmniErrorService.errorOccurred(enrolment, BahmniErrorType.NoPatientWithId);
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
