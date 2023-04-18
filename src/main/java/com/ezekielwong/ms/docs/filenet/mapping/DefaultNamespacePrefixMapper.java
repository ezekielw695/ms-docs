package com.ezekielwong.ms.docs.filenet.mapping;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Prefix namespace mapper for SOAP requests
 */
@Component
public class DefaultNamespacePrefixMapper extends NamespacePrefixMapper {

    private final Map<String, String> namespaceMap = new HashMap<>();

    public DefaultNamespacePrefixMapper() {
        namespaceMap.put("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        namespaceMap.put("http://www.w3.org/2001/XMLSchema", "xsd");
        namespaceMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
    }

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return namespaceMap.getOrDefault(namespaceUri, suggestion);
    }
}
