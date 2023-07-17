package org.avni_integration_service.integration_data.service.error;

public interface ErrorClassifierForGoonjTestConstants {
    String STRING_FORMAT_BUFFETED_ERROR_MSG = "<<Head -- %s -- tail>>";
    String ERROR_MSG_STANDARD_SKIP = "aafafasdg adfad1232r @SDA!2CGAE7670$%.!";
    String ERROR_MSG_DISPATCH_MISSING_DEMAND = "Individual not found with UUID 'null' or External ID 'a1CHE0000001qqz2AA'";
    String ERROR_MSG_INVALID_VALUE_FOR_RESTRICTED_PICKLIST = "INVALID_OR_NULL_FOR_RESTRICTED_PICKLIST, District: bad value for restricted picklist field";
    String ERROR_MSG_DISPATCH_RECEIPT_DUPLICATE = "System.ListException: Before Insert or Upsert list must not have two identically equal elements";
    String ERROR_MSG_FIELD_CUSTOM_VALIDATION_EXCEPTION = "FIELD_CUSTOM_VALIDATION_EXCEPTION";
    String ERROR_MSG_DISPATCH_RECEIPT_INVALID_RECEIVED_DATE = "FIELD_CUSTOM_VALIDATION_EXCEPTION, Received Date can not be prior to related DispatchDtae";
    String ERROR_MSG_DISPATCH_RECEIPT_DUPLICATES = "FIELD_CUSTOM_VALIDATION_EXCEPTION, More than one Dispatch Received Status cannot be created for a Dispatch Status.";
    String ERROR_MSG_DISPATCH_RECEIPT_LINE_ITEM_MISMATCH = "FIELD_CUSTOM_VALIDATION_EXCEPTION, Material Received can not be inserted or updated as Related Dispatch Status have no Line Item";
    String ERROR_MSG_DISTRIBUTION_DISASTER_MISSING = "FIELD_CUSTOM_VALIDATION_EXCEPTION, Please fill in the Disaster Type, if the Type of Initiative contains Rahat.";
    String ERROR_MSG_ACTIVITY_MEASUREMENT_TYPE_MISSING = "FIELD_CUSTOM_VALIDATION_EXCEPTION, Please fill in Measurement Type.";
}
