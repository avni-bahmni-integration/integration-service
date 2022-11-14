package org.avni_integration_service.amrit.config;

public interface BeneficiaryConstant {
    String SUBJECT_TYPE = "Individual";
    String FETCH_AMRIT_ID_RESOURCE_PATH = "/rmnch/getAmritIdForAvniId";
    String UPSERT_AMRIT_BENEFICIARY_RESOURCE_PATH = "/rmnch/syncDataToAmrit/new";
    String IDENTITY_TYPE = "identityType";
    String NATIONAL_ID = "National ID";
    String CREATED_BY = "createdBy";

}
