package com.ezekielwong.ms.docs.domain.response.thirdpartyapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Third party app access token API response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
}
