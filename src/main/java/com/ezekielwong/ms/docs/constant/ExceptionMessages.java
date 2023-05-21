package com.ezekielwong.ms.docs.constant;

public class ExceptionMessages {

    // Enum errors
    public static final String MSDB_CASE_ID_NOT_FOUND_MSG = "Unable to find request case id in MSDB";
    public static final String MSDB_TEMPLATE_NAME_MISMATCH_MSG = "Template name in request does not match the one in MSDB for the given case id";
    public static final String REQUEST_VALIDATION_ERROR_MSG = "Request validation error, unable to parse request";
    public static final String INVALID_JWT_EXCEPTION_MSG = "Error validating JSON web token";
    public static final String MSDB_PSQL_EXCEPTION_MSG = "MSDB encountered PSQL exception while executing";

    // Call errors
    public static final String THIRD_PARTY_APP_CALL_ERROR_MSG = "Unable to call third party app";
    public static final String FILENET_CALL_ERROR_MSG = "Unable to call filenet";

    // Unknown error
    public static final String UNKNOWN_ERROR_MSG = "Unknown error";
}
