package com.ezekielw.ms.docs.constant;

public class ExceptionMessages {

    // Enum errors
    public static final String DB_CASE_ID_NOT_FOUND_MSG = "Unable to find request case id in DB";
    public static final String DB_TEMPLATE_NAME_MISMATCH_MSG = "Template name in request does not match the one in DB for the given case id";
    public static final String REQUEST_VALIDATION_ERROR_MSG = "Request validation error, unable to parse request";
    public static final String DATA_LAKE_MISSING_DOCUMENT_PROPERTY_MSG = "Missing data lake document property in metadata";
    public static final String INVALID_JWT_EXCEPTION_MSG = "Error validating JSON web token";
    public static final String DB_SQL_EXCEPTION_MSG = "DB encountered SQL exception while executing";

    // Call errors
    public static final String THIRD_PARTY_APP_CALL_ERROR_MSG = "Unable to call third party app";
    public static final String DATA_LAKE_CALL_ERROR_MSG = "Unable to call data lake";

    // Unknown error
    public static final String UNKNOWN_ERROR_MSG = "Unknown error";
}
