package org.avni_integration_service.integration_data.domain.error;

import org.avni_integration_service.integration_data.domain.framework.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class ErrorRecordLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_record_id")
    private ErrorRecord errorRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "error_type_id")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorRecordLog that = (ErrorRecordLog) o;
        return errorRecord.equals(that.errorRecord) && errorType.equals(that.errorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorRecord, errorType);
    }
}
