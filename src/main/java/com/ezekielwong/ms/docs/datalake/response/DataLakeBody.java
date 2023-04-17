package com.ezekielwong.ms.docs.datalake.response;

import com.ezekielwong.ms.docs.datalake.request.DataLakeRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"datalakecontext", "datalakerequest", "datalakeresponse"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataLakeBody implements Serializable {

    private DataLakeContext datalakecontext;
    private DataLakeRequest datalakerequest;
    private DataLakeResponse datalakereponse;
}
