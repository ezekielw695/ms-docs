package com.ezekielwong.ms.docs.domain.response.thirdpartyapp;

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

    private String name;
    private String startDate;
    private String status;
    private String info;
}
