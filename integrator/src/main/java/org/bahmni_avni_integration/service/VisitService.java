package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSVisitRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {
    private final ConstantsRepository constantsRepository;
    private final OpenMRSVisitRepository openMRSVisitRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private static final Logger logger = Logger.getLogger(VisitService.class);

    public VisitService(ConstantsRepository constantsRepository, OpenMRSVisitRepository openMRSVisitRepository, MappingMetaDataRepository mappingMetaDataRepository) {
        this.constantsRepository = constantsRepository;
        this.openMRSVisitRepository = openMRSVisitRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public OpenMRSVisit getAvniRegistrationVisit(String patientUuid) {
        Constants allConstants = constantsRepository.findAllConstants();
        String locationUuid = allConstants.getValue(ConstantKey.IntegrationBahmniLocation);
        String visitTypeUuid = allConstants.getValue(ConstantKey.IntegrationBahmniVisitType);
        return openMRSVisitRepository.getVisit(patientUuid, locationUuid, visitTypeUuid);
    }

    public OpenMRSVisit getAvniEnrolmentVisit(String patientUuid, String enrolmentUUID, String programUUID) {
        var visitTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment,
                MappingType.CommunityEnrolment_VisitType,
                programUUID);
        var avniUuidVisitAttributeTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniUUID_VisitAttributeType);
        String locationUuid = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        var visits = openMRSVisitRepository.getVisits(patientUuid, locationUuid, visitTypeUuid);
        return visits.stream()
                .filter(visit -> matchesEnrolmentId(visit, enrolmentUUID, avniUuidVisitAttributeTypeUuid))
                .findFirst().orElse(null);
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient, Subject subject) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        String visitType = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniVisitType);
        return createVisit(patient, location, visitType, visitAttributes(subject));
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient, Enrolment enrolment) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        String visitType = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolment_VisitType, enrolment.getProgram());
        return createVisit(patient, location, visitType, visitAttributes(enrolment));
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient, String location, String visitType, List<OpenMRSSaveVisitAttribute> visitAttributes) {
        OpenMRSSaveVisit openMRSSaveVisit = new OpenMRSSaveVisit();
        openMRSSaveVisit.setLocation(location);
        openMRSSaveVisit.setVisitType(visitType);
        openMRSSaveVisit.setPatient(patient.getUuid());
        String startDatetime = FormatAndParseUtil.toISODateString(
                FormatAndParseUtil.fromIsoDateString(patient.getAuditInfo().getDateCreated()));
        openMRSSaveVisit.setStartDatetime(startDatetime);
        openMRSSaveVisit.setAttributes(visitAttributes);
        OpenMRSVisit visit = openMRSVisitRepository.createVisit(openMRSSaveVisit);
        logger.debug("Created new visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    private List<OpenMRSSaveVisitAttribute> visitAttributes(Subject subject) {
        String avniIdAttributeType = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniUUID_VisitAttributeType);
        var avniIdAttribute = new OpenMRSSaveVisitAttribute();
        avniIdAttribute.setAttributeType(avniIdAttributeType);
        avniIdAttribute.setValue(subject.getUuid());

        String avniEventDateAttributeType = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniEventDate_VisitAttributeType);
        var eventDateAttribute = new OpenMRSSaveVisitAttribute();
        eventDateAttribute.setAttributeType(avniEventDateAttributeType);
        eventDateAttribute.setValue(FormatAndParseUtil.toISODateString(subject.getRegistrationDate()));

        return List.of(avniIdAttribute, eventDateAttribute);
    }

    private List<OpenMRSSaveVisitAttribute> visitAttributes(Enrolment enrolment) {
        String avniIdAttributeType = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniUUID_VisitAttributeType);
        var avniIdAttribute = new OpenMRSSaveVisitAttribute();
        avniIdAttribute.setAttributeType(avniIdAttributeType);
        avniIdAttribute.setValue(enrolment.getUuid());

        String avniEventDateAttributeType = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniEventDate_VisitAttributeType);
        var eventDateAttribute = new OpenMRSSaveVisitAttribute();
        eventDateAttribute.setAttributeType(avniEventDateAttributeType);
        eventDateAttribute.setValue(FormatAndParseUtil.toISODateString(enrolment.getEnrolmentDateTime()));

        return List.of(avniIdAttribute, eventDateAttribute);
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient, Subject subject) {
        var visit = getAvniRegistrationVisit(patient.getUuid());
        if (visit == null) {
            return createVisit(patient, subject);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient, Enrolment enrolment) {
        var visit = getAvniEnrolmentVisit(patient.getUuid(), enrolment.getUuid(), enrolment.getProgram());
        if (visit == null) {
            return createVisit(patient, enrolment);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    private boolean matchesEnrolmentId(OpenMRSVisit visit, String enrolmentUUID, String avniUuidVisitAttributeTypeUuid) {
        return visit.getAttributes().stream().anyMatch(visitAttribute ->
                visitAttribute.getAttributeType().getUuid().equals(avniUuidVisitAttributeTypeUuid)
                && visitAttribute.getValue().equals(enrolmentUUID));
    }

    public void voidVisit(Enrolment enrolment, OpenMRSFullEncounter communityEnrolmentEncounter) {
        Constants allConstants = constantsRepository.findAllConstants();
        String locationUuid = allConstants.getValue(ConstantKey.IntegrationBahmniLocation);
        String visitType = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment, MappingType.CommunityEnrolment_VisitType, enrolment.getProgram());
        OpenMRSVisit visit = openMRSVisitRepository.getVisit(communityEnrolmentEncounter.getPatient().getUuid(), locationUuid, visitType);
        openMRSVisitRepository.deleteVisit(visit.getUuid());
    }
}
