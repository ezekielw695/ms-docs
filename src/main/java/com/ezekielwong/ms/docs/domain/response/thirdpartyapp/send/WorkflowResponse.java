package com.ezekielwong.ms.docs.domain.response.thirdpartyapp.send;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Third party app workflow API response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

    /**
     * Name of the workflow
     */
    private String name;

    /**
     * Date and time the workflow was started
     */
    private String startDate;

    /**
     * Status of the workflow
     */
    private String status;

    /**
     * Additional information associated with the workflow
     */
    private String info;
}
