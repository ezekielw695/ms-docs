package com.ezekielwong.ms.docs.domain.response.thirdpartyapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Third party app access token error API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenErrorResponse {

    private String error;
    private String errorDesc;
}
