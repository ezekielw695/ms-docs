package com.ezekielwong.ms.docs.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo {

    private List<ErrorDetail> errorDetailList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {

        private String errorCode;
        private String errorDescription;
        private List<ProviderError> providerErrorList;
    }
}
