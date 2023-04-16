package com.ezekielwong.ms.docs.service.impl;

import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.entity.Docs;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.repository.DocsRepository;
import com.ezekielwong.ms.docs.service.ClientService;
import com.ezekielwong.ms.docs.utils.XmlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Optional;

import static com.ezekielwong.ms.docs.constant.ExceptionEnum.DB_TEMPLATE_NAME_MISMATCH;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.DB_TEMPLATE_NAME_MISMATCH_MSG;

/**
 * Service for client requests
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final XmlUtils xmlUtils;

    private final DocsRepository docsRepository;

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
     * @return {@link Docs} containing the saved/updated data
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Docs saveOrUpdate(ClientWorkflowRequest clientRequest) {

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
                log.error(DB_TEMPLATE_NAME_MISMATCH_MSG);
                String errMsg = DB_TEMPLATE_NAME_MISMATCH_MSG + String.format(": [ Request: \"%s\", DB: \"%s\" ]",
                        clientRequest.getName(), docs.getTemplateName());

                throw new GenericBadException(DB_TEMPLATE_NAME_MISMATCH, errMsg);
            }

            docs.setUpdatedBy(clientRequest.getUpdatedChannel());
        }

        // Remaining data
        docs.setFieldDataList(clientRequest.getFieldDataList());
        docs.setRequesterInfo(clientRequest.getRequesterInfo());

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

        return docsRepository.saveAndFlush(docsDB);
    }
}
