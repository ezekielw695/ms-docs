package com.ezekielw.ms.docs.domain.response.thirdpartyapp.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Third party app access token error API response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenErrorResponse {

    private String error;

    private String errorDesc;
}
