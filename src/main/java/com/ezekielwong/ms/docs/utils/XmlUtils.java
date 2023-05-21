package com.ezekielwong.ms.docs.utils;


import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.domain.request.client.common.RequesterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Utility class for XML parsing
 */
@Slf4j
@Component
public class XmlUtils {

    /**
     * Generate XML string from the client workflow JSON object
     *
     * @param clientRequest {@link ClientWorkflowRequest}
     * @return XML string used when starting the workflow
     * @throws ParserConfigurationException DocumentBuilder unable to satisfy the configuration requested
     * @throws IllegalAccessException Underlying field is inaccessible
     * @throws TransformerException Error encountered during transformation
     */
    public String json2Xml(ClientWorkflowRequest clientRequest)
            throws ParserConfigurationException, IllegalAccessException, TransformerException {

        log.debug("Converting client workflow JSON object into XML string");
        String requestCaseId = clientRequest.getCaseId();
        List<FieldData> fieldDataList = clientRequest.getFieldDataList();
        RequesterInfo requestRequesterInfo = clientRequest.getRequesterInfo();

        // Create new Document object
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // Root element
        log.debug("Creating root element");
        Element root = document.createElement("TemplateFieldData");
        document.appendChild(root);

        // Case id element
        log.debug("Creating case id element");
        Element caseId = document.createElement("CaseId");
        caseId.appendChild(document.createTextNode(requestCaseId));
        root.appendChild(caseId);

        // Field data elements
        log.debug("Creating field data elements");
        for (FieldData fieldData : fieldDataList) {

            // Ignore empty field values
            if (StringUtils.isNotBlank(fieldData.getValue())) {

                log.debug(fieldData.getFieldId() + " : " + fieldData.getValue());

                Element e = document.createElement(fieldData.getFieldId());
                e.appendChild(document.createTextNode(fieldData.getValue()));
                root.appendChild(e);
            }
        }

        // Requester info element
        log.debug("Creating requester info element");
        Element requesterInfo = document.createElement("RequesterInfo");
        root.appendChild(requesterInfo);

        for (Field field : requestRequesterInfo.getClass().getDeclaredFields()) {

            // Field is a complex object
            if (StringUtils.equalsAnyIgnoreCase(field.getName(), "Manager")) {

                Element manager = document.createElement("Manager");
                requesterInfo.appendChild(manager);
                RequesterInfo.Manager requestManager = requestRequesterInfo.getManager();

                for (Field f : requestManager.getClass().getDeclaredFields()) {
                    mapElement(document, manager, requestManager, f);
                }

            // Field is not a complex object
            } else {
                mapElement(document, requesterInfo, requestRequesterInfo, field);
            }
        }

        // Generate XML string
        log.debug("Generating XML string");
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);

        StringWriter sw = new StringWriter();
        StreamResult streamResult = new StreamResult(sw);
        transformer.transform(domSource, streamResult);

        String xml = sw.toString();
        log.debug(xml);

        return xml;
    }

    /**
     * Map object field name and value
     *
     * @param document XML document
     * @param element Parent element
     * @param object Object to be mapped
     * @param field Object fields to be mapped
     * @throws IllegalAccessException Underlying field is inaccessible
     */
    private void mapElement(Document document, Element element, Object object, Field field) throws IllegalAccessException {

        log.debug(field.toString());
        ReflectionUtils.makeAccessible(field);

        // Ignore empty field values
        if (StringUtils.isNotBlank((String) field.get(object))) {

            Element e = document.createElement(StringUtils.capitalize(field.getName()));
            e.appendChild(document.createTextNode((String) field.get(object)));
            element.appendChild(e);
        }
    }
}
