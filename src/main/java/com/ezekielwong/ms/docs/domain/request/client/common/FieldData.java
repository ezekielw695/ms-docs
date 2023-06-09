package com.ezekielwong.ms.docs.domain.request.client.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldData implements Serializable {

    @NotBlank
    @JsonProperty("FieldId")
    private String fieldId;

    @NotNull
    @JsonProperty("Value")
    private String value;
}
