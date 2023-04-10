package com.ezekielw.ms.docs.domain.request.thirdpartyapp;

import com.ezekielw.ms.docs.domain.request.BaseApiRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Third party app upload document API request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UploadDocumentRequest extends BaseApiRequest {

    /**
     * Unique case identifier
     */
    @NotBlank
    private String caseId;

    /**
     * Document metadata in XML format
     */
    @NotBlank
    private String metadata;

    /**
     * Document in base64 format
     */
    @NotBlank
    private String document;
}
