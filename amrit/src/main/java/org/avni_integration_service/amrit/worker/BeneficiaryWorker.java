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
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.IntegratingEntityStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BeneficiaryWorker implements BeneficiaryConstant, ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(BeneficiaryWorker.class);
    private final BeneficiaryService beneficiaryService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final IntegratingEntityStatusService integratingEntityStatusService;
    private final AvniAmritErrorService avniAmritErrorService;
    private final AmritEntityType amritEntityType;

    public BeneficiaryWorker(BeneficiaryService beneficiaryService,
                             IntegratingEntityStatusRepository integratingEntityStatusRepository,
                             AvniSubjectRepository avniSubjectRepository,
                             IntegratingEntityStatusService integratingEntityStatusService,
                             AvniAmritErrorService avniAmritErrorService) {
        this.beneficiaryService = beneficiaryService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.integratingEntityStatusService = integratingEntityStatusService;
        this.avniAmritErrorService = avniAmritErrorService;
        this.amritEntityType = AmritEntityType.Beneficiary;
    }

    public void syncBeneficiariesFromAvniToAmrit() {
        processSubjects(AmritEntityType.Beneficiary);
    }

    public void scanSyncStatusOfBeneficiariesFromAvniToAmrit() {
        processSubjects(AmritEntityType.BeneficiaryScan);
    }

    public void processSubjects(AmritEntityType entityType) {
        while (true) {
            IntegratingEntityStatus beneficiarySyncStatus = integratingEntityStatusRepository.findByEntityType(AmritEntityType.Beneficiary.name());
            IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(entityType.name());
            SubjectsResponse response = avniSubjectRepository.getSubjects(status.getReadUptoDateTime(), SUBJECT_TYPE);
            Subject[] subjects = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, status.getReadUptoDateTime()));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                if (entityType.equals(AmritEntityType.Beneficiary)) {
                    processSubject(entityType, subject, true);
                } else if (entityType.equals(AmritEntityType.BeneficiaryScan)) {
                    if (beneficiarySyncAttempted(subject, beneficiarySyncStatus)) {
                        checkIfSubjectWasSavedSuccessfully(entityType, subject, true);
                    } else {
                        logger.warn("Stopped processing as sync has not yet been attempted " +
                                "for entities with lastModifiedDate " + subject.getLastModifiedDate());
                        break;
                    }
                } else {
                    throw new UnsupportedOperationException("AmritEntityType " + entityType + " is not supported");
                }
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSubject(AmritEntityType entityType, Subject subject, boolean updateSyncStatus) {
        logger.debug("Processing subject %s".formatted(subject.getUuid()));
        beneficiaryService.createOrUpdateBeneficiary(subject);
        updateSyncStatus(entityType, subject, updateSyncStatus);
    }

    protected void checkIfSubjectWasSavedSuccessfully(AmritEntityType entityType, Subject subject, boolean updateSyncStatus) {
        logger.debug("Processing subject %s".formatted(subject.getUuid()));
        beneficiaryService.wasFetchOfAmritIdSuccessful(subject, true);
        updateSyncStatus(entityType, subject, updateSyncStatus);
    }

    private boolean beneficiarySyncAttempted(Subject subject, IntegratingEntityStatus beneficiarySyncStatus) {
        return subject.getLastModifiedDate().before(beneficiarySyncStatus.getReadUptoDateTime());
    }

    private void updateSyncStatus(AmritEntityType entityType, Subject subject, boolean updateSyncStatus) {
        if (updateSyncStatus) {//TODO check if getLastModifiedDate stored has valid time component and can be used in next sync
            integratingEntityStatusService.saveEntityStatus(entityType.name(), subject.getLastModifiedDate());
        }
    }

    @Override
    public void processError(String entityUuid) throws Exception {
        Subject beneficiary = avniSubjectRepository.getSubject(entityUuid);
        if (beneficiary == null) {
            String message = String.format("Subject has been deleted now: %s", entityUuid);
            logger.warn(message);
            avniAmritErrorService.errorOccurred(entityUuid, AmritErrorType.EntityIsDeleted, AvniEntityType.Subject, message);
            return;
        }
        processSubject(amritEntityType, beneficiary, false);
    }
}
