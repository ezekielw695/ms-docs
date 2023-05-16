package com.ezekielwong.ms.docs.domain.request.ms.send;

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
    private String name;

    /**
     * XML data used when starting the workflow
     */
    private String params;
}
