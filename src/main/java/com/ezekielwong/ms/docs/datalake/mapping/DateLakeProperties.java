package com.ezekielwong.ms.docs.datalake.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Date lake properties referenced in application.properties
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateLakeProperties implements Serializable {

    @JsonProperty("DocumentTitle")
    private String documentTitle;

    @JsonProperty("DocumentType")
    private String documentType;
}
