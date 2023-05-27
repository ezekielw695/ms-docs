package com.ezekielwong.ms.docs.service.impl;

import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.entity.Docs;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.exception.common.GenericSuccessException;
import com.ezekielwong.ms.docs.repository.DocsRepository;
import com.ezekielwong.ms.docs.service.ClientService;
import com.ezekielwong.ms.docs.utils.XmlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ezekielwong.ms.docs.constant.ExceptionEnum.MSDB_TEMPLATE_NAME_MISMATCH;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.REQUEST_VALIDATION_ERROR;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.MSDB_TEMPLATE_NAME_MISMATCH_MSG;

/**
 * Service for client requests
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final XmlUtils xmlUtils;

    private final DocsRepository docsRepository;

    private final Map<String, String> mapping;

    @Value("#{'${filenet.doc-prop.prop-name-list}'.split(',')}")
    private List<String> propNameList;

    /**
     * Extract the document properties required by Filenet
     *
     * @param fieldDataList List of workflow data
     * @return Map of the document properties required by Filenet
     */
    @Override
    public Map<String, String> extractDocProps(List<FieldData> fieldDataList) {

        log.debug("Extracting document properties");
        Map<String, String> fieldDataMap;

        try {
            fieldDataMap = fieldDataList.stream().collect(Collectors.toMap(FieldData::getFieldId, FieldData::getValue));

        } catch (IllegalStateException e) {

            String errMsg = e.getMessage();
            log.error(errMsg);

            throw new GenericBadException(REQUEST_VALIDATION_ERROR, errMsg);
        }

        Map<String, String> docPropsMap = new LinkedHashMap<>(2);

        propNameList.forEach(propName -> {

            String mappingValue = mapping.get(propName);
            String propValue = fieldDataMap.get(mappingValue);

            log.debug("PropName: [ {} ] -> PropValue: [ {} ]", propName, propValue);
            docPropsMap.put(propName, propValue);
        });

        return docPropsMap;
    }

    /**
     * Generate XML string from client workflow request JSON object
     *
     * @param clientRequest {@link ClientWorkflowRequest}
     * @return XML string used when starting the workflow
     * @throws ParserConfigurationException DocumentBuilder unable to satisfy the configuration requested
     * @throws IllegalAccessException Underlying field is inaccessible
     * @throws TransformerException Error encountered during transformation
     */
    @Override
    public String generateXmlString(ClientWorkflowRequest clientRequest)
            throws ParserConfigurationException, IllegalAccessException, TransformerException {
        return xmlUtils.json2Xml(clientRequest);
    }

    /**
     * Save/update client workflow request
     *
     * @param clientRequest {@link ClientWorkflowRequest}
     * @param docPropsMap Map of the document properties required by filenet
     * @return {@link Docs} containing the saved/updated data
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Docs saveOrUpdate(ClientWorkflowRequest clientRequest, Map<String, String> docPropsMap) {

        log.debug("Saving/updating client request");
        Optional<Docs> docsDB = Optional.ofNullable(docsRepository.findByCaseId(clientRequest.getCaseId()));

        Docs docs;

        // New request
        if (docsDB.isEmpty()) {

            log.debug("New request");
            docs = new Docs();

            docs.setCreatedBy(clientRequest.getUpdatedChannel());
            docs.setCaseId(clientRequest.getCaseId());
            docs.setTemplateName(clientRequest.getName());

        // Update request
        } else {

            log.debug("Update request");
            docs = docsDB.get();

            // Check for template name mismatch
            if (!StringUtils.equals(clientRequest.getName(), docs.getTemplateName())) {

                log.error(MSDB_TEMPLATE_NAME_MISMATCH_MSG);
                String errMsg = MSDB_TEMPLATE_NAME_MISMATCH_MSG + String.format(": [ Request: \"%s\", DB: \"%s\" ]",
                        clientRequest.getName(), docs.getTemplateName());

                throw new GenericSuccessException(MSDB_TEMPLATE_NAME_MISMATCH, errMsg);
            }

            docs.setUpdatedBy(clientRequest.getUpdatedChannel());
        }

        // Remaining data
        docs.setFieldDataList(clientRequest.getFieldDataList());
        docs.setRequesterInfo(clientRequest.getRequesterInfo());
        docs.setDocPropsMap(docPropsMap);

        return docsRepository.saveAndFlush(docs);
    }

    /**
     * Update client workflow request status
     *
     * @param caseId Unique client workflow request case identifier
     * @param status Current status of the request
     * @return {@link Docs} containing in the updated status
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Docs updateStatus(String caseId, String status) {

        log.debug("Updating client request status");
        Docs docsDB = docsRepository.findByCaseId(caseId);
        docsDB.setStatus(status);
        docsDB.setUpdatedBy("SYSTEM");

        return docsRepository.saveAndFlush(docsDB);
    }
}
