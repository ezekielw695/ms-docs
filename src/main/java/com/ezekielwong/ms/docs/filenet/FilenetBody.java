package com.ezekielwong.ms.docs.filenet;

import com.ezekielwong.ms.docs.filenet.request.FilenetRequest;
import com.ezekielwong.ms.docs.filenet.response.FilenetResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"filenetcontext", "filenetrequest", "filenetresponse"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilenetBody implements Serializable {

    private FilenetContext filenetcontext;
    private FilenetRequest filenetrequest;
    private FilenetResponse filenetresponse;
}
