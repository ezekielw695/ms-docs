package com.ezekielwong.ms.docs.domain.request.client;

import com.ezekielwong.ms.docs.domain.request.BaseApiRequest;
import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.domain.request.client.common.RequesterInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Client workflow API request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientWorkflowRequest extends BaseApiRequest {

    /**
     * Unique client workflow request case identifier
     */
    @NotBlank
    @JsonProperty("CaseId")
    private String caseId;

    /**
     * Name of the workflow
     */
    @NotBlank
    @JsonProperty("Name")
    private String name;

    /**
     * List of workflow data
     */
    @Valid
    @NotEmpty
    @JsonProperty("FieldDataList")
    private List<FieldData> fieldDataList;

    /**
     * Requester details
     */
    @NotNull
    @JsonProperty("RequesterInfo")
    private RequesterInfo requesterInfo;
}
