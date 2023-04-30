package com.ezekielwong.ms.docs.exception.thirdpartyapp;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.exception.common.BaseException;
import lombok.NonNull;

public class ThirdPartyAppNullResponseException extends BaseException {

    public ThirdPartyAppNullResponseException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
