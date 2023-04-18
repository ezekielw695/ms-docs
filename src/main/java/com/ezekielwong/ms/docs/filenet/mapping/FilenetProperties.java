package com.ezekielwong.ms.docs.filenet.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Filenet properties referenced in application.properties
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilenetProperties implements Serializable {

    @JsonProperty("DocumentTitle")
    private String documentTitle;

    @JsonProperty("DocumentType")
    private String documentType;
}
