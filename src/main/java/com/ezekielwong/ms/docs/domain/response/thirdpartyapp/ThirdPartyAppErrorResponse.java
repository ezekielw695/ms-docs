package com.ezekielwong.ms.docs.domain.response.thirdpartyapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Third party app error API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyAppErrorResponse {

    /**
     * Error response object
     */
    @JsonProperty("Error")
    private Error error;

    /**
     * List of validation errors
     */
    @JsonProperty("ValidationErrors")
    private List<ValidationError> validationErrorList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {

        private Integer httpStatusCode;
        private Integer errorCode;
        private String errorMessage;
        private String referenceId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {

        private Integer errorCode;
        private String propertyName;
        private String errorMessage;
    }
}
