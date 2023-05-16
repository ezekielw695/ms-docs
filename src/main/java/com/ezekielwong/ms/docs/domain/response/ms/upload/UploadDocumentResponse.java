package com.ezekielwong.ms.docs.domain.response.ms.upload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadDocumentResponse {

    /**
     * Unique client workflow request case identifier
     */
    private String caseId;
}
