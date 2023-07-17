package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorRecordLog;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorRecordLogRepository;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.integration_data.repository.ErrorTypeRepository;
import org.avni_integration_service.integration_data.repository.UserRepository;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.web.contract.ErrorWebContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;

@RestController
@PreAuthorize("hasRole('USER')")
public class ErrorRecordLogController extends BaseController{
    private final ErrorRecordLogRepository errorRecordLogRepository;
    private final ErrorRecordRepository errorRecordRepository;
    private final ErrorTypeRepository errorTypeRepository;

    @Autowired
    public ErrorRecordLogController(UserRepository userRepository, ErrorRecordLogRepository errorRecordLogRepository,
                                    ErrorRecordRepository errorRecordRepository, ErrorTypeRepository errorTypeRepository) {
        super(userRepository);
        this.errorRecordLogRepository = errorRecordLogRepository;
        this.errorRecordRepository = errorRecordRepository;
        this.errorTypeRepository = errorTypeRepository;
    }

    @RequestMapping(value = "/int/errorRecordLog", method = {RequestMethod.GET})
    public Page<ErrorWebContract> getPage(Pageable pageable, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        return toContractPage(errorRecordLogRepository.findAllByErrorRecordIntegrationSystem(currentIntegrationSystem, pageable));
    }

    @RequestMapping(value = "/int/errorRecordLog/{id}", method = {RequestMethod.GET})
    public ErrorWebContract get(@PathVariable("id") Integer id) {
        ErrorRecordLog errorRecordLog = errorRecordLogRepository.findById(id).get();
        return getErrorWebContract(errorRecordLog);
    }

    @RequestMapping(value = "/int/errorRecordLog/{id}", method = {RequestMethod.PUT})
    @Transactional
    public ErrorWebContract update(@PathVariable("id") Integer id, @RequestBody ErrorWebContract errorWebContract) {
        ErrorRecordLog errorRecordLog = errorRecordLogRepository.findById(id).get();
        errorRecordLog.getErrorRecord().setProcessingDisabled(errorWebContract.isProcessingDisabled());
        errorRecordRepository.save(errorRecordLog.getErrorRecord());
        return getErrorWebContract(errorRecordLog);
    }

    private Page<ErrorWebContract> toContractPage(Page<ErrorRecordLog> page) {
        return page.map(this::getErrorWebContract);
    }

    private ErrorWebContract getErrorWebContract(ErrorRecordLog errorRecordLog) {
        ErrorWebContract errorWebContract = new ErrorWebContract();
        errorWebContract.setId(errorRecordLog.getId());
        errorWebContract.setErrorType(errorRecordLog.getErrorType().getValue());
        errorWebContract.setLoggedAt(errorRecordLog.getLoggedAt());
        errorWebContract.setErrorMsg(errorRecordLog.getErrorMsg());
        errorWebContract.setProcessingDisabled(errorRecordLog.getErrorRecord().isProcessingDisabled());
        errorWebContract.setIntegrationSystem(errorRecordLog.getErrorRecord().getIntegrationSystem().getSystemType().name());
        errorWebContract.setIntegrationSystemInstance(errorRecordLog.getErrorRecord().getIntegrationSystem().getName());
        AvniEntityType avniEntityType = errorRecordLog.getErrorRecord().getAvniEntityType();
        if (avniEntityType != null)
            errorWebContract.setAvniEntityType(avniEntityType.name());

        String integratingEntityType = errorRecordLog.getErrorRecord().getIntegratingEntityType();
        if (integratingEntityType != null) {
            errorWebContract.setIntegratingEntityType(integratingEntityType);
        }
        errorWebContract.setEntityId(errorRecordLog.getErrorRecord().getEntityId());
        return errorWebContract;
    }

    @RequestMapping(value = "/int/errorRecordLog/search/findByEntity", method = {RequestMethod.GET})
    public Page<ErrorWebContract> findByEntityId(@RequestParam("entityId") String entityId, Pageable pageable, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        return toContractPage(errorRecordLogRepository.findAllByErrorRecordEntityIdContainsAndErrorRecordIntegrationSystem
                (entityId, currentIntegrationSystem, pageable));
    }

    @RequestMapping(value = "/int/errorRecordLog/search/findByErrorType", method = {RequestMethod.GET})
    public Page<ErrorWebContract> findByErrorType(@RequestParam("errorType") int errorType, Pageable pageable, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        ErrorType et = errorTypeRepository.findEntity(errorType);
        return toContractPage(errorRecordLogRepository.findAllByErrorTypeAndErrorRecordIntegrationSystem(et, currentIntegrationSystem, pageable));
    }

    @RequestMapping(value = "/int/errorRecordLog/search/findByStartDate", method = {RequestMethod.GET})
    public Page<ErrorWebContract> findByStartDate(@RequestParam("startDate") String startDate, Pageable pageable, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        return toContractPage(errorRecordLogRepository.findAllByLoggedAtAfterAndErrorRecordIntegrationSystem
                (FormatAndParseUtil.fromAvniDate(startDate), currentIntegrationSystem, pageable));
    }

    @RequestMapping(value = "/int/errorRecordLog/search/findByEndDate", method = {RequestMethod.GET})
    public Page<ErrorWebContract> findByEndDate(@RequestParam("endDate") String endDate, Pageable pageable, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        return toContractPage(errorRecordLogRepository.findAllByLoggedAtBeforeAndErrorRecordIntegrationSystem
                (FormatAndParseUtil.fromAvniDate(endDate), currentIntegrationSystem, pageable));
    }

    @RequestMapping(value = "/int/errorRecordLog/search/find", method = {RequestMethod.GET})
    public Page<ErrorWebContract> find(@RequestParam(value = "startDate", required = false) String startDate,
                                                    @RequestParam(value = "endDate", required = false) String endDate,
                                                    @RequestParam(value = "errorType", required = false) Integer errorType,
                                                    @RequestParam(value = "entityId", required = false) String entityId,
                                                    Pageable pageable, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        if (startDate != null && endDate != null)
            return toContractPage(errorRecordLogRepository.findAllByLoggedAtAfterAndLoggedAtBeforeAndErrorRecordIntegrationSystem
                    (FormatAndParseUtil.fromAvniDate(startDate), FormatAndParseUtil.fromAvniDate(endDate), currentIntegrationSystem, pageable));
        else if (errorType != null && entityId != null) {
            ErrorType et = errorTypeRepository.findEntity(errorType);
            return toContractPage(errorRecordLogRepository.findAllByErrorTypeAndErrorRecordEntityIdContainsAndErrorRecordIntegrationSystem
                    (et, entityId.trim(), currentIntegrationSystem, pageable));
        }
        throw new RuntimeException("Invalid usage of find");
    }
}
