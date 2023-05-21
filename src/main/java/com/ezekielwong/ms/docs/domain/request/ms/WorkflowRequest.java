package com.ezekielwong.ms.docs.domain.request.ms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MS workflow API request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest implements Serializable {

    /**
     * Name of the workflow
     */
    @JsonProperty("Name")
    private String name;

    /**
     * XML data used when starting the workflow
     */
    @JsonProperty("Params")
    private String params;
}
