package com.ezekielwong.ms.docs.domain.request.client;

import com.ezekielwong.ms.docs.domain.request.BaseApiRequest;
import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.domain.request.client.common.RequesterInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Client workflow API request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientWorkflowRequest extends BaseApiRequest {

    /**
     * Unique client case identifier
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
    @NotBlank
    @JsonProperty("FieldDataList")
    private List<FieldData> fieldDataList;

    /**
     * Requester details
     */
    @NotNull
    @JsonProperty("RequesterInfo")
    private RequesterInfo requesterInfo;
}
