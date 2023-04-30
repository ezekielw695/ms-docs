package com.ezekielwong.ms.docs.exception.filenet;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.exception.common.BaseException;
import lombok.NonNull;

public class FilenetNullResponseBodyException extends BaseException {

    public FilenetNullResponseBodyException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
