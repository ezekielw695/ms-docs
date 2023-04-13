package com.ezekielwong.ms.docs.constant;

import java.util.HashMap;
import java.util.Map;

public enum ExceptionEnum {

    // 2xx Successful
    DB_CASE_ID_NOT_FOUND("2001"),
    DB_TEMPLATE_NAME_MISMATCH("2002"),

    // 4xx Client Error
    REQUEST_VALIDATION_ERROR("4001"),
    DATA_LAKE_MISSING_DOCUMENT_PROPERTY("4002"),
    INVALID_JWT_EXCEPTION("4003"),

    // 5xx Server Error
    DB_SQL_EXCEPTION("5001"),
    THIRD_PARTY_APP_CALL_ERROR("5002"),
    DATA_LAKE_CALL_ERROR("5003"),
    UNKNOWN_ERROR("5999");

    private final String errorCode;

    private final Map<String, String> errorDescriptionMap = new HashMap<>();

    ExceptionEnum(String errorCode) {
        this.errorCode = errorCode;

        // 2xx Successful
        this.errorDescriptionMap.put("2001", ExceptionMessages.DB_CASE_ID_NOT_FOUND_MSG);
        this.errorDescriptionMap.put("2002", ExceptionMessages.DB_TEMPLATE_NAME_MISMATCH_MSG);

        // 4xx Client Error
        this.errorDescriptionMap.put("4001", ExceptionMessages.REQUEST_VALIDATION_ERROR_MSG);
        this.errorDescriptionMap.put("4002", ExceptionMessages.DATA_LAKE_MISSING_DOCUMENT_PROPERTY_MSG);
        this.errorDescriptionMap.put("4003", ExceptionMessages.INVALID_JWT_EXCEPTION_MSG);

        // 5xx Server Error
        this.errorDescriptionMap.put("5001", ExceptionMessages.DB_SQL_EXCEPTION_MSG);
        this.errorDescriptionMap.put("5002", ExceptionMessages.THIRD_PARTY_APP_CALL_ERROR_MSG);
        this.errorDescriptionMap.put("5003", ExceptionMessages.DATA_LAKE_CALL_ERROR_MSG);
        this.errorDescriptionMap.put("5999", ExceptionMessages.UNKNOWN_ERROR_MSG);
    }

    public String getCode() {
        return this.errorCode;
    }

    public String getDescription() {
        return this.errorDescriptionMap.get(this.errorCode);
    }
}
