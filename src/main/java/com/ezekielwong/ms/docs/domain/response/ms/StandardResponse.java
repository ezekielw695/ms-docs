package com.ezekielwong.ms.docs.domain.response.ms;

import com.ezekielwong.ms.docs.error.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MS API response
 *
 * @param <T> Response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponse<T> {

    private T data;
    private String message;
    private String status;
    private ErrorResponse errorResponse;
}
