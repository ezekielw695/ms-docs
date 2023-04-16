package com.ezekielwong.ms.docs.domain.response.ms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentResponse {

    /**
     * Unique client workflow request case identifier
     */
    private String caseId;
}
