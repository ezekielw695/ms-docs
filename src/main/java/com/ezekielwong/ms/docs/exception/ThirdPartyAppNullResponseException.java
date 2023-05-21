package com.ezekielwong.ms.docs.exception;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.exception.common.GenericException;
import lombok.NonNull;

public class ThirdPartyAppNullResponseException extends GenericException {

    public ThirdPartyAppNullResponseException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
