package com.ezekielwong.ms.docs.domain.request.thirdpartyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentDto {

    private String correlationId;
    private String dateTime;
    private String updatedChannel;

    private String documentId;
    private String name;
    private String caseId;

    private Map<String, Map<String, String>> metadata;
    private Map<String, String> docPropsMap;

    private String docContent;
}
