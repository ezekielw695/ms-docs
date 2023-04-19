package com.ezekielwong.ms.docs.filenet;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"messageid", "hostname", "timestamp", "userid"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilenetContext implements Serializable {

    private String messageid;
    private String hostname;
    private String timestamp;
    private String userid;
}
