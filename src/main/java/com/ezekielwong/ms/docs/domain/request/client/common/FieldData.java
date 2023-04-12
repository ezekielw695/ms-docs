package com.ezekielwong.ms.docs.domain.request.client.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldData implements Serializable {

    private String fieldId;

    private String value;
}
