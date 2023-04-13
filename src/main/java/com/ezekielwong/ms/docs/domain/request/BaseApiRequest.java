package com.ezekielwong.ms.docs.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseApiRequest {

    /**
     * Take from request header: x-correlation-id
     */
    @JsonIgnore
    private String correlationId;

    /**
     * Take from request header: x-source-country
     */
    @JsonIgnore
    private String apiCountryCode;

    /**
     * Take from request header: x-source-date-time
     */
    @JsonIgnore
    private String dateTime;

    /**
     * Take from request header: x-source-id
     */
    @JsonIgnore
    private String updatedChannel;
}
