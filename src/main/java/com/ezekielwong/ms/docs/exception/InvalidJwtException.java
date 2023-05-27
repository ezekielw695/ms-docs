package com.ezekielwong.ms.docs.exception;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.exception.common.BaseException;
import lombok.NonNull;

public class InvalidJwtException extends BaseException {

    public InvalidJwtException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
