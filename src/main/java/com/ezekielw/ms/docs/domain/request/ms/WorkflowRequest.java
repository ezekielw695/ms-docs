package com.ezekielw.ms.docs.domain.request.ms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MS workflow API request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest implements Serializable {

    /**
     * Name of the workflow
     */
    private String name;

    /**
     * Data used when starting the workflow, in XML format
     */
    private String params;
}
