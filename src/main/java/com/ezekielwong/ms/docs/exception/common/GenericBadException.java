package com.ezekielwong.ms.docs.exception.common;

import com.ezekielwong.ms.docs.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class GenericBadException extends BaseException {

    public GenericBadException(@NonNull ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
