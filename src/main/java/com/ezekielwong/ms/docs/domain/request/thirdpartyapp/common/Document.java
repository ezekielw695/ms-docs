package com.ezekielwong.ms.docs.domain.request.thirdpartyapp.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document implements Serializable {

    @NotBlank
    @JsonProperty("Id")
    private String id;

    @NotBlank
    @JsonProperty("Description")
    private String description;

    @NotBlank
    @JsonProperty("Name")
    private String name;

    @NotBlank
    @JsonProperty("CaseId")
    private String caseId;

    @Valid
    @NotEmpty
    @JsonProperty("Metadata")
    private List<DocData> docDataList;

    @NotBlank
    @JsonProperty("documentBase64")
    private String docContent;
}
