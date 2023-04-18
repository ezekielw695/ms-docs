package com.ezekielwong.ms.docs.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderError {

    private String providerErrorCode;
    private String providerErrorDetail;
}
