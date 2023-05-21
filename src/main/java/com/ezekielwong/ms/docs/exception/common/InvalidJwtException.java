package com.ezekielwong.ms.docs.exception.common;

import com.ezekielwong.ms.docs.error.ErrorCode;
import lombok.NonNull;

public class InvalidJwtException extends BaseException {

    public InvalidJwtException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
