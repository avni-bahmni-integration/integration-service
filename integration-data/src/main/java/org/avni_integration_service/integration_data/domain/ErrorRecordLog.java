package org.avni_integration_service.integration_data.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ErrorRecordLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_record_id")
    private ErrorRecord errorRecord;

    @Column
    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    @Column(name = "logged_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loggedAt;

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public void setLoggedAt(Date date) {
        this.loggedAt = date;
    }

    public void setErrorRecord(ErrorRecord errorRecord) {
        this.errorRecord = errorRecord;
    }

    public ErrorRecord getErrorRecord() {
        return errorRecord;
    }

    public Date getLoggedAt() {
        return loggedAt;
    }
}
