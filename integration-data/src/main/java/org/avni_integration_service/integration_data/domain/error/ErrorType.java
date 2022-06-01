package org.avni_integration_service.integration_data.domain.error;

import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;

@Entity
public class ErrorType extends NamedIntegrationSpecificEntity {
    @Column
    private String name;

    public ErrorType() {

    }
    public ErrorType(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return this.getId();
    }
}
