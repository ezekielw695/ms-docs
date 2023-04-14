package com.ezekielwong.ms.docs.error;

import com.ezekielwong.ms.docs.exception.common.BaseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String errorCode;
    private String description;
    private ErrorInfo errorInfo;

    public ErrorResponse (BaseException exception) {
        this.errorCode = exception.getErrorCode().getCode();
        this.description = exception.getMessage();
        this.errorInfo = exception.getErrorInfo();
    }

    public ErrorResponse (String errorCode, String description, ErrorInfo errorInfo) {
        this.errorCode = errorCode;
        this.description = description;
        this.errorInfo = errorInfo;
    }
}
