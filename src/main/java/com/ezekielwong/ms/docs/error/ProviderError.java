package com.ezekielwong.ms.docs.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProviderError {

    private String providerErrorCode;
    private String providerErrorDetail;
}
