package com.ezekielwong.ms.docs.domain.response.thirdpartyapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Third party app workflow API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

    /**
     * Name of the workflow
     */
    @JsonProperty("Name")
    private String name;

    /**
     * Date and time the workflow was started
     */
    @JsonProperty("StartDate")
    private String startDate;

    /**
     * Status of the workflow
     */
    @JsonProperty("Status")
    private String status;

    /**
     * Additional information associated with the workflow
     */
    @JsonProperty("Info")
    private String info;
}
