package com.ezekielwong.ms.docs.filenet.request;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"documentclass", "objectstore", "docprops"})
public class FilenetRequest {

    private String documentclass;
    private String objectstore;
    private docprops docprops;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class docprops implements Serializable {

        private List<DocProp> docPropList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"propname", "proptype", "propvalue"})
    public static class DocProp implements Serializable {

        private String propname;
        private String proptype;
        private String propvalue;
    }
}
