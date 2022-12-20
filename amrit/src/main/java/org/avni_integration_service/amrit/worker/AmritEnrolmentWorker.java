package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.amrit.service.BeneficiaryService;
import org.avni_integration_service.amrit.service.BornBirthService;
import org.avni_integration_service.amrit.service.CBACService;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.EnrolmentsResponse;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEnrolmentRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.IntegratingEntityStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class AmritEnrolmentWorker implements ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(AmritEnrolmentWorker.class);
    private final BeneficiaryService beneficiaryService;
    private final BornBirthService bornBirthService;
    private final CBACService cBACService;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final IntegratingEntityStatusService integratingEntityStatusService;
    private final AvniAmritErrorService avniAmritErrorService;
    private final AmritEntityType amritEntityType;

    public AmritEnrolmentWorker(BeneficiaryService beneficiaryService,
                                BornBirthService bornBirthService, CBACService cBACService,
                                AvniEnrolmentRepository avniEnrolmentRepository,
                                IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                AvniSubjectRepository avniSubjectRepository,
                                IntegratingEntityStatusService integratingEntityStatusService,
                                AvniAmritErrorService avniAmritErrorService) {
        this.beneficiaryService = beneficiaryService;
        this.bornBirthService = bornBirthService;
        this.cBACService = cBACService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.integratingEntityStatusService = integratingEntityStatusService;
        this.avniAmritErrorService = avniAmritErrorService;
        this.amritEntityType = AmritEntityType.Beneficiary;
    }

    public void syncEnrolmentsFromAvniToAmrit(AmritEntityType entityType) {
        processEnrolments(entityType);
    }

    public void processEnrolments(AmritEntityType entityType) {
        while (true) {
            IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(entityType.name());
            Date readUptoDateTime = getEffectiveCutoffDateTime(status);
            EnrolmentsResponse response = avniEnrolmentRepository.getEnrolments(readUptoDateTime, entityType.getDbName());
            Enrolment[] generalEnrolments = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d generalEnrolments that are newer than %s", generalEnrolments.length, readUptoDateTime));
            if (generalEnrolments.length == 0) break;
            for (Enrolment enrolment : generalEnrolments) {
                processEnrolment(enrolment, true, entityType);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processEnrolment(Enrolment enrolment, boolean updateSyncStatus, AmritEntityType entityType) {
        logger.debug("%s Processing enrolment  %s".formatted(entityType, enrolment.getUuid()));
        if (shouldFilterEnrolment(enrolment)) {
            logger.warn(String.format("%s General enrolment should be filtered out: %s", entityType, enrolment.getUuid()));
            updateSyncStatus(enrolment, updateSyncStatus, entityType);
            return;
        }

        Subject beneficiary = avniSubjectRepository.getSubject(enrolment.getSubjectId());
        logger.debug(String.format("Found avni beneficiary %s", beneficiary.getUuid()));
        if (beneficiary.getVoided()) {
            logger.debug(String.format("Avni beneficiary is voided. Skipping. %s", beneficiary.getUuid()));
            updateSyncStatus(enrolment, updateSyncStatus, entityType);
            return;
        }

        if(entityType.equals(AmritEntityType.BornBirth)) {
            bornBirthService.createOrUpdateBornBirth(beneficiary, enrolment);
        } else {
            throw new AssertionError(String.format("Cannot process Avni enrolment for entityType %s", entityType));
        }

        updateSyncStatus(enrolment, updateSyncStatus, entityType);
    }

    private boolean shouldFilterEnrolment(Enrolment generalEnrolment) {
        return generalEnrolment.isExited() || generalEnrolment.getVoided();
    }

    private void updateSyncStatus(Enrolment enrolment, boolean updateSyncStatus, AmritEntityType entityType) {
        if (updateSyncStatus) {//TODO check if getLastModifiedDate stored has valid time component and can be used in next sync
            integratingEntityStatusService.saveEntityStatus(entityType.name(), enrolment.getLastModifiedDate());
        }
    }

    @Override
    public void processError(ErrorRecord errorRecord) {
        String entityUuid = errorRecord.getEntityId();
        Enrolment enrolment = avniEnrolmentRepository.getEnrolment(entityUuid);
        if (enrolment == null) {
            String message = String.format("%s enrolment has been deleted now: %s", errorRecord.getIntegratingEntityType(), entityUuid);
            logger.warn(message);
            avniAmritErrorService.errorOccurred(entityUuid, AmritErrorType.EntityIsDeleted, AvniEntityType.Enrolment, message);
            return;
        }

        if(errorRecord.getIntegratingEntityType() != null) {
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.BornBirth.name())) {
                processEnrolment(enrolment, false, AmritEntityType.BornBirth);
            } else if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.CBAC.name())) {
                processEnrolment(enrolment, false, AmritEntityType.CBAC);
            } else {
                throw new AssertionError(String.format("Cannot handle error record with AvniEntityType=%s / AmritEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
            }
        } else {
            throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s / AmritEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
        }
    }
}
