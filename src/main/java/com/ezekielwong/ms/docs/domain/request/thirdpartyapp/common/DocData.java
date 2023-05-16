package com.ezekielwong.ms.docs.domain.request.thirdpartyapp.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocData implements Serializable {

    @NotBlank
    @JsonProperty("Group")
    private String group;

    @NotBlank
    @JsonProperty("Field")
    private String field;

    @NotBlank
    @JsonProperty("Value")
    private String value;
}
