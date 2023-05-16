package com.ezekielwong.ms.docs.service;

import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.entity.Docs;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.Map;

public interface ClientService {

    Map<String, String> extractDocProps(List<FieldData> fieldDataList);

    String generateXmlString(ClientWorkflowRequest clientRequest)
        throws ParserConfigurationException, IllegalAccessException, TransformerException;

    Docs saveOrUpdate(ClientWorkflowRequest clientRequest, Map<String, String> docPropsMap);

    Docs updateStatus(String caseId, String status);
}
