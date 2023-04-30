package com.ezekielwong.ms.docs.constant;

import com.ezekielwong.ms.docs.error.ErrorCode;

import java.util.HashMap;
import java.util.Map;

import static com.ezekielwong.ms.docs.constant.ExceptionMessages.*;

public enum ExceptionEnum implements ErrorCode {

    // 2xx Successful
    DB_CASE_ID_NOT_FOUND("2001"),
    DB_TEMPLATE_NAME_MISMATCH("2002"),

    // 4xx Client Error
    REQUEST_VALIDATION_ERROR("4001"),
    FILENET_MISSING_DOCUMENT_PROPERTY("4002"),
    INVALID_JWT_EXCEPTION("4003"),

    // 5xx Server Error
    DB_PSQL_EXCEPTION("5001"),
    THIRD_PARTY_APP_CALL_ERROR("5002"),
    FILENET_CALL_ERROR("5003"),
    UNKNOWN_ERROR("5999");

    private final String errorCode;

    private final Map<String, String> errorDescriptionMap = new HashMap<>();

    ExceptionEnum(String errorCode) {
        this.errorCode = errorCode;

        // 2xx Successful
        this.errorDescriptionMap.put("2001", DB_CASE_ID_NOT_FOUND_MSG);
        this.errorDescriptionMap.put("2002", DB_TEMPLATE_NAME_MISMATCH_MSG);

        // 4xx Client Error
        this.errorDescriptionMap.put("4001", REQUEST_VALIDATION_ERROR_MSG);
        this.errorDescriptionMap.put("4002", FILENET_MISSING_DOCUMENT_PROPERTY_MSG);
        this.errorDescriptionMap.put("4003", INVALID_JWT_EXCEPTION_MSG);

        // 5xx Server Error
        this.errorDescriptionMap.put("5001", DB_PSQL_EXCEPTION_MSG);
        this.errorDescriptionMap.put("5002", THIRD_PARTY_APP_CALL_ERROR_MSG);
        this.errorDescriptionMap.put("5003", FILENET_CALL_ERROR_MSG);
        this.errorDescriptionMap.put("5999", UNKNOWN_ERROR_MSG);
    }

    @Override
    public String getCode() {
        return this.errorCode;
    }

    @Override
    public String getDescription() {
        return this.errorDescriptionMap.get(this.errorCode);
    }
}
