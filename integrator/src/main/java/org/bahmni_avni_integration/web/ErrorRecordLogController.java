package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecordLog;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordLogRepository;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.web.response.ErrorWebContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/")
public class ErrorRecordLogController {
    private final ErrorRecordLogRepository errorRecordLogRepository;
    private final ErrorRecordRepository errorRecordRepository;

    @Autowired
    public ErrorRecordLogController(ErrorRecordLogRepository errorRecordLogRepository, ErrorRecordRepository errorRecordRepository) {
        this.errorRecordLogRepository = errorRecordLogRepository;
        this.errorRecordRepository = errorRecordRepository;
    }

    @RequestMapping(value = {"/errorRecordLog"}, method = {RequestMethod.GET})
    public Page<ErrorWebContract> getPage(Pageable pageable) {
        return toContractPage(errorRecordLogRepository.findAll(pageable));
    }

    @RequestMapping(value = "/errorRecordLog/{id}", method = {RequestMethod.GET})
    @PreAuthorize("hasRole('USER')")
    public ErrorWebContract get(@PathVariable("id") Integer id) {
        ErrorRecordLog errorRecordLog = errorRecordLogRepository.findById(id).get();
        return getErrorWebContract(errorRecordLog);
    }

    @RequestMapping(value = "/errorRecordLog/{id}", method = {RequestMethod.PUT})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ErrorWebContract update(@PathVariable("id") Integer id, @RequestBody ErrorWebContract errorWebContract) {
        ErrorRecordLog errorRecordLog = errorRecordLogRepository.findById(id).get();
        errorRecordLog.getErrorRecord().setProcessingDisabled(errorWebContract.isProcessingDisabled());
        errorRecordRepository.save(errorRecordLog.getErrorRecord());
        return getErrorWebContract(errorRecordLog);
    }

    private Page<ErrorWebContract> toContractPage(Page<ErrorRecordLog> page) {
        return page.map(errorRecordLog -> {
            return getErrorWebContract(errorRecordLog);
        });
    }

    private ErrorWebContract getErrorWebContract(ErrorRecordLog errorRecordLog) {
        ErrorWebContract errorWebContract = new ErrorWebContract();
        errorWebContract.setId(errorRecordLog.getId());
        errorWebContract.setErrorType(errorRecordLog.getErrorType().getValue());
        errorWebContract.setLoggedAt(errorRecordLog.getLoggedAt());
        errorWebContract.setProcessingDisabled(errorRecordLog.getErrorRecord().isProcessingDisabled());
        AvniEntityType avniEntityType = errorRecordLog.getErrorRecord().getAvniEntityType();
        if (avniEntityType != null)
            errorWebContract.setAvniEntityType(avniEntityType.name());
        BahmniEntityType bahmniEntityType = errorRecordLog.getErrorRecord().getBahmniEntityType();
        if (bahmniEntityType != null)
            errorWebContract.setBahmniEntityType(bahmniEntityType.name());
        errorWebContract.setEntityUuid(errorRecordLog.getErrorRecord().getEntityId());
        return errorWebContract;
    }

    @RequestMapping(value = "/errorRecordLog/search/findByEntity")
    public Page<ErrorWebContract> findByEntityId(@RequestParam("entityId") String entityId, Pageable pageable) {
        return toContractPage(errorRecordLogRepository.findAllByErrorRecordEntityIdContains(entityId, pageable));
    }

    @RequestMapping(value = "/errorRecordLog/search/findByErrorType")
    public Page<ErrorWebContract> findByErrorType(@RequestParam("errorType") int errorType, Pageable pageable) {
        return toContractPage(errorRecordLogRepository.findAllByErrorType(ErrorType.findByValue(errorType), pageable));
    }

    @RequestMapping(value = "/errorRecordLog/search/findByStartDate")
    public Page<ErrorWebContract> findByStartDate(@RequestParam("startDate") String startDate, Pageable pageable) {
        return toContractPage(errorRecordLogRepository.findAllByLoggedAtAfter(FormatAndParseUtil.fromAvniDate(startDate), pageable));
    }

    @RequestMapping(value = "/errorRecordLog/search/findByEndDate")
    public Page<ErrorWebContract> findByEndDate(@RequestParam("endDate") String endDate, Pageable pageable) {
        return toContractPage(errorRecordLogRepository.findAllByLoggedAtBefore(FormatAndParseUtil.fromAvniDate(endDate), pageable));
    }

    @RequestMapping(value = "/errorRecordLog/search/find")
    public Page<ErrorWebContract> find(@RequestParam(value = "startDate", required = false) String startDate,
                                                    @RequestParam(value = "endDate", required = false) String endDate,
                                                    @RequestParam(value = "errorType", required = false) Integer errorType,
                                                    @RequestParam(value = "entityId", required = false) String entityId,
                                                    Pageable pageable) {
        if (startDate != null && endDate != null)
            return toContractPage(errorRecordLogRepository.findAllByLoggedAtAfterAndLoggedAtBefore(FormatAndParseUtil.fromAvniDate(startDate), FormatAndParseUtil.fromAvniDate(endDate), pageable));
        else if (errorType != null && entityId != null)
            return toContractPage(errorRecordLogRepository.findAllByErrorTypeAndErrorRecordEntityIdContains(ErrorType.findByValue(errorType), entityId.trim(), pageable));
        throw new RuntimeException("Invalid usage of find");
    }
}
