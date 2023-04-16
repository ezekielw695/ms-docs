package com.ezekielwong.ms.docs.service;

import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.entity.Docs;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface ClientService {

    String generateXmlString(ClientWorkflowRequest clientRequest)
        throws ParserConfigurationException, IllegalAccessException, TransformerException;

    Docs saveOrUpdate(ClientWorkflowRequest clientRequest);

    Docs updateStatus(String caseId, String status);
}
