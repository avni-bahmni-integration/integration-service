package org.avni_integration_service.integration_data.domain.error;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ErrorType extends NamedIntegrationSpecificEntity {

    @Column(name = "comparison_operator")
    private ErrorTypeComparisonOperatorEnum comparisonOperator;

    @Column(name = "comparison_value", columnDefinition = "TEXT")
    private String comparisonValue;

    public ErrorType() {
    }
    public ErrorType(String name, IntegrationSystem integrationSystem) {
        this.setName(name);
        this.setIntegrationSystem(integrationSystem);
    }

    public ErrorType(String name, IntegrationSystem integrationSystem, ErrorTypeComparisonOperatorEnum comparisonOperator, String comparisonValue) {
        this.setName(name);
        this.setIntegrationSystem(integrationSystem);
        this.setComparisonOperator(comparisonOperator);
        this.setComparisonValue(comparisonValue);
    }

    public ErrorType(String name, IntegrationSystem integrationSystem, String comparisonOperator, String comparisonValue) {
        this.setName(name);
        this.setIntegrationSystem(integrationSystem);
        this.setComparisonOperator(comparisonOperator);
        this.setComparisonValue(comparisonValue);
    }

    public ErrorTypeComparisonOperatorEnum getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ErrorTypeComparisonOperatorEnum comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public void setComparisonOperator(String comparisonOperator) {
        this.comparisonOperator = ErrorTypeComparisonOperatorEnum.valueOf(comparisonOperator);
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ErrorType et = (ErrorType) obj;
        return (obj != null && getName().equals(et.getName()));
    }
}
