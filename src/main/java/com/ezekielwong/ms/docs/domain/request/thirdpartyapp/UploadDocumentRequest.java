package com.ezekielwong.ms.docs.domain.request.thirdpartyapp;

import com.ezekielwong.ms.docs.domain.request.BaseApiRequest;
import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.common.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Third party app upload document API request
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UploadDocumentRequest extends BaseApiRequest {

    @Valid
    @NotNull
    @JsonProperty("Documents")
    private Document document;
}
