package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.config.BeneficiaryConstant;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.amrit.service.BeneficiaryService;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.domain.SubjectsResponse;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.IntegratingEntityStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BeneficiaryWorker implements BeneficiaryConstant {
    private static final Logger logger = Logger.getLogger(BeneficiaryWorker.class);
    private final BeneficiaryService beneficiaryService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniAmritErrorService avniAmritErrorService;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final IntegratingEntityStatusService integratingEntityStatusService;

    public BeneficiaryWorker(BeneficiaryService beneficiaryService, IntegratingEntityStatusRepository integratingEntityStatusRepository, AvniSubjectRepository avniSubjectRepository, AvniAmritErrorService avniAmritErrorService, IntegratingEntityStatusService integratingEntityStatusService) {
        this.beneficiaryService = beneficiaryService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.avniAmritErrorService = avniAmritErrorService;
        this.integratingEntityStatusService = integratingEntityStatusService;
    }

    //TODO
    public void syncBeneficiariesFromAvniToAmrit() {
        processSubjects();
    }

    public void processSubjects() {
        while (true) {
            IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(AmritEntityType.BENEFICIARY.name()); //TODO add entry in int-ent-status table for beneficiary for amrit system
            SubjectsResponse response = avniSubjectRepository.getSubjects(status.getReadUptoDateTime(), SUBJECT_TYPE);
            Subject[] subjects = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, status.getReadUptoDateTime()));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                processSubject(subject, true);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    private void updateSyncStatus(Subject subject, boolean updateSyncStatus) {
        if (updateSyncStatus) //TODO check if getLastModifiedDate stored has valid time component
            integratingEntityStatusService.saveEntityStatus(AmritEntityType.BENEFICIARY.name(), subject.getLastModifiedDate());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSubject(Subject subject, boolean updateSyncStatus) {
        logger.debug("Processing subject %s".formatted(subject.getUuid()));
        try {
            beneficiaryService.createOrUpdateBeneficiary(subject);
        } catch (Exception e) {
            avniAmritErrorService.errorOccurred(subject.getUuid(), AmritErrorType.BeneficiaryCreationError, AmritEntityType.BENEFICIARY, e.getLocalizedMessage());
        }
        updateSyncStatus(subject, updateSyncStatus);
    }
}
