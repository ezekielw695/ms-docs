package com.ezekielwong.ms.docs.datalake.response;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"guid", "version", "status"})
public class DataLakeResponse implements Serializable {

    private String guid;
    private String version;
    private Status status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"code", "type", "desc"})
    public static class Status implements Serializable {

        private String code;
        private String type;
        private String desc;
    }
}
