package com.ezekielwong.ms.docs.exception.common;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.error.ErrorInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    @NonNull
    private ErrorCode errorCode;

    private ErrorInfo errorInfo;

    public BaseException (@NonNull ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorInfo = null;
    }

    public BaseException (@NonNull ErrorCode errorCode, String message) {
        super((message == null ? errorCode.getDescription() : message), null);
        this.errorCode = errorCode;
        this.errorInfo = null;
    }
}
