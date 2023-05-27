package com.ezekielwong.ms.docs.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderError {

    private String providerErrorCode;
    private String providerErrorDetail;
}
