package org.avni_integration_service.amrit.config;

public interface BeneficiaryConstant {
    String SUBJECT_TYPE = "Individual";
    String FETCH_AMRIT_ID_RESOURCE_PATH = "/rmnch/getAmritIdForAvniId";
    String UPSERT_AMRIT_BENEFICIARY_RESOURCE_PATH = "/rmnch/syncDataToAmrit/new";
    String IDENTITY_TYPE = "identityType";
    String NATIONAL_ID = "National ID";
    String CREATED_BY = "createdBy";
    String VAN_ID = "vanID";
    String LITERACY_STATUS = "literacyStatus";
    String EDUCATION_NAME = "educationName";
    String LOCATION = "location";
    String STATE_ID = "stateID";
    String DISTRICT_ID = "districtID";
    String BLOCK_ID = "blockID";
    String PANCHAYAT_ID = "panchayatID";
    String DISTRICT_BRANCH_ID = "districtBranchID";
    String VILLAGE_EXTERNAL_ID = "Village External ID";
    String PANCHAYAT_EXTERNAL_ID = "Panchayat External ID";
    String BLOCK_EXTERNAL_ID = "Block External ID";
    String DISTRICT_EXTERNAL_ID = "District External ID";
    String STATE_EXTERNAL_ID = "State External ID";
    String BENEFICIARY_REG_ID = "beneficiaryRegID";
    int VAN_ID_VALUE = 61;
}
