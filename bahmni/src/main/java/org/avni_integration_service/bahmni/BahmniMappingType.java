package org.avni_integration_service.bahmni;

import org.apache.log4j.Logger;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.repository.MappingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BahmniMappingType {
    private static final Logger logger = Logger.getLogger(org.avni_integration_service.bahmni.BahmniMappingType.class);

    public final MappingType subjectEncounterType;
    public final MappingType personAttributeConcept;
    public final MappingType encounterType;
    public final MappingType labEncounterType;
    public final MappingType drugOrderEncounterType;
    public final MappingType drugOrderConcept;
    public final MappingType communityEnrolmentEncounterType;
    public final MappingType communityEnrolmentExitEncounterType;
    public final MappingType communityProgramEncounterEncounterType;
    public final MappingType communityEncounterEncounterType;
    public final MappingType avniUUIDConcept;
    public final MappingType avniEventDateConcept;
    public final MappingType avniProgramDataConcept;
    public final MappingType bahmniUUIDConcept;
    public final MappingType bahmniFormCommunityProgram;
    public final MappingType communityRegistrationBahmniForm;
    public final MappingType communityEnrolmentBahmniForm;
    public final MappingType communityEnrolmentExitBahmniForm;
    public final MappingType communityProgramEncounterBahmniForm;
    public final MappingType communityEncounterBahmniForm;
    public final MappingType patientIdentifierConcept;
    public final MappingType communityEnrolmentVisitType;
    public final MappingType avniEventDateVisitAttributeType;
    public final MappingType avniUUIDVisitAttributeType;
    public final MappingType concept;

    @Autowired
    public BahmniMappingType(MappingTypeRepository mappingTypeRepository) {
        this.subjectEncounterType = mappingTypeRepository.findByName("Subject_EncounterType");
        this.personAttributeConcept = mappingTypeRepository.findByName("PersonAttributeConcept");
        this.encounterType = mappingTypeRepository.findByName("EncounterType");
        this.labEncounterType = mappingTypeRepository.findByName("LabEncounterType");
        this.drugOrderEncounterType = mappingTypeRepository.findByName("DrugOrderEncounterType");
        this.drugOrderConcept = mappingTypeRepository.findByName("DrugOrderConcept");
        this.communityEnrolmentEncounterType = mappingTypeRepository.findByName("CommunityEnrolment_EncounterType");
        this.communityEnrolmentExitEncounterType = mappingTypeRepository.findByName("CommunityEnrolmentExit_EncounterType");
        this.communityProgramEncounterEncounterType = mappingTypeRepository.findByName("CommunityProgramEncounter_EncounterType");
        this.communityEncounterEncounterType = mappingTypeRepository.findByName("CommunityEncounter_EncounterType");
        this.avniUUIDConcept = mappingTypeRepository.findByName("AvniUUID_Concept");
        this.avniEventDateConcept = mappingTypeRepository.findByName("AvniEventDate_Concept");
        this.avniProgramDataConcept = mappingTypeRepository.findByName("AvniProgramData_Concept");
        this.bahmniUUIDConcept = mappingTypeRepository.findByName("BahmniUUID_Concept");
        this.bahmniFormCommunityProgram = mappingTypeRepository.findByName("BahmniForm_CommunityProgram");
        this.communityRegistrationBahmniForm = mappingTypeRepository.findByName("CommunityRegistration_BahmniForm");
        this.communityEnrolmentBahmniForm = mappingTypeRepository.findByName("CommunityEnrolment_BahmniForm");
        this.communityEnrolmentExitBahmniForm = mappingTypeRepository.findByName("CommunityEnrolmentExit_BahmniForm");
        this.communityProgramEncounterBahmniForm = mappingTypeRepository.findByName("CommunityProgramEncounter_BahmniForm");
        this.communityEncounterBahmniForm = mappingTypeRepository.findByName("CommunityEncounter_BahmniForm");
        this.patientIdentifierConcept = mappingTypeRepository.findByName("PatientIdentifier_Concept");
        this.communityEnrolmentVisitType = mappingTypeRepository.findByName("CommunityEnrolment_VisitType");
        this.avniEventDateVisitAttributeType = mappingTypeRepository.findByName("AvniEventDate_VisitAttributeType");
        this.avniUUIDVisitAttributeType = mappingTypeRepository.findByName("AvniUUID_VisitAttributeType");
        this.concept = mappingTypeRepository.findByName("Concept");
    }
}
