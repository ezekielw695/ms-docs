package com.ezekielwong.ms.docs.exception.thirdpartyapp;

import com.ezekielwong.ms.docs.error.ErrorCode;
import com.ezekielwong.ms.docs.exception.common.BaseException;
import lombok.NonNull;

public class ThirdPartyAppDocPropMissingException extends BaseException {

    public ThirdPartyAppDocPropMissingException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
