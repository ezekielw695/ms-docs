package com.ezekielwong.ms.docs.domain.request.client.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldData implements Serializable {

    @JsonProperty("FieldId")
    private String fieldId;

    @JsonProperty("Value")
    private String value;
}
