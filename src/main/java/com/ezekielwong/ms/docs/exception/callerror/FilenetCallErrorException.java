package com.ezekielwong.ms.docs.exception.callerror;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.exception.common.BaseException;
import lombok.NonNull;

public class FilenetCallErrorException extends BaseException {

    public FilenetCallErrorException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
