package com.ezekielwong.ms.docs.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorInfo {

    private List<ErrorDetail> errorDetailList;

    @Data
    @AllArgsConstructor
    public static class ErrorDetail {

        private String errorCode;
        private String errorDescription;
        private List<ProviderError> providerErrorList;
    }
}
