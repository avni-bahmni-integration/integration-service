package org.avni_integration_service.web.contract;

import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.domain.error.ErrorTypeComparisonOperatorEnum;
import org.avni_integration_service.integration_data.domain.framework.BaseEnum;
import org.avni_integration_service.integration_data.domain.framework.NamedEntity;

public class ErrorTypeContract extends NamedEntityContract{

    private ErrorTypeComparisonOperatorEnum comparisonOperator;
    private String comparisonValue;

    public ErrorTypeContract(BaseEnum baseEnum, ErrorTypeComparisonOperatorEnum comparisonOperator, String comparisonValue) {
        super(baseEnum);
        this.comparisonOperator = comparisonOperator;
        this.comparisonValue = comparisonValue;
    }

    public ErrorTypeContract(int id, String name, ErrorTypeComparisonOperatorEnum comparisonOperator, String comparisonValue) {
        super(id, name);
        this.comparisonOperator = comparisonOperator;
        this.comparisonValue = comparisonValue;
    }

    public ErrorTypeContract(NamedEntity namedEntity, ErrorTypeComparisonOperatorEnum comparisonOperator, String comparisonValue) {
        super(namedEntity);
        this.comparisonOperator = comparisonOperator;
        this.comparisonValue = comparisonValue;
    }

    public ErrorTypeContract(ErrorType errorType) {
        this(errorType.getId(), errorType.getName(), errorType.getComparisonOperator(), errorType.getComparisonValue());
    }

    public ErrorTypeComparisonOperatorEnum getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ErrorTypeComparisonOperatorEnum comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

}
