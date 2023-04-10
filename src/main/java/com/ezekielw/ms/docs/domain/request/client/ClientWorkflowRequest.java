package com.ezekielw.ms.docs.domain.request.client;

import com.ezekielw.ms.docs.domain.request.BaseApiRequest;
import com.ezekielw.ms.docs.domain.request.client.common.FieldData;
import com.ezekielw.ms.docs.domain.request.client.common.RequesterInfo;
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
     * Unique case identifier
     */
    @NotBlank
    private String caseId;

    /**
     * Name of the workflow
     */
    @NotBlank
    private String name;

    /**
     * List of workflow data
     */
    @NotBlank
    private List<FieldData> fieldDataList;

    /**
     * Requester details
     */
    @NotNull
    private RequesterInfo requesterInfo;
}
