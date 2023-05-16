package com.ezekielwong.ms.docs.controller;

import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.domain.response.ms.StandardResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.send.WorkflowResponse;
import com.ezekielwong.ms.docs.service.ClientService;
import com.ezekielwong.ms.docs.service.WebClientService;
import com.ezekielwong.ms.docs.utils.DateTimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Map;

import static com.ezekielwong.ms.docs.constant.Constants.*;

/**
 * Controller for client requests
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ClientController extends BaseController {

    private final HttpServletRequest httpServletRequest;

    private final ClientService clientService;

    private final WebClientService webclientService;

    private final DateTimeUtils dateTimeUtils;

    @PostMapping("/workflow/start")
    public ResponseEntity<StandardResponse<Object>> startWorkflow (@RequestBody @Valid ClientWorkflowRequest clientRequest)
        throws ParserConfigurationException, IllegalAccessException, TransformerException,
            NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        LocalDateTime startTime = LocalDateTime.now();
        log.debug("### Start Workflow began at: {} ###", dateTimeUtils.getLocalDateTime(startTime));
        
        setBaseRequest(clientRequest, httpServletRequest);
        String caseId = clientRequest.getCaseId();
        
        // Extract the document properties required by filenet
        Map<String, String> docPropsMap = clientService.extractDocProps(clientRequest.getFieldDataList());
        log.info("Document properties extracted");

        // Generate XML string from client workflow request JSON object
        String params = clientService.generateXmlString(clientRequest);
        log.info("Client workflow JSON object converted into XML string");

        // Save/update client workflow request
        clientService.saveOrUpdate(clientRequest, docPropsMap);
        log.info("Client request saved/updated");

        // Send client workflow request to third party app
        Object response = webclientService.sendWorkflow(caseId, clientRequest.getName(), params);
        log.info("Workflow response received from third party app");

        // Success response with workflow details
        if (response.getClass() == WorkflowResponse.class) {

            // Update status to TPA_REQ_SENT
            clientService.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT);
            log.info(STATUS_UPDATED);

            LocalDateTime endTime = LocalDateTime.now();
            log.debug("### Start Workflow ended at: {} ###", dateTimeUtils.getLocalDateTime(endTime));
            log.info("### REQUEST TOOK {} SECONDS ###", dateTimeUtils.getTimeTaken(startTime, endTime));

            return ResponseEntity.ok(createSuccessResponse(response, START_WORKFLOW_SUCCESS));

        // Failure response with error details
        } else {

            // Update status to TPA_REQ_SENT_ERROR
            clientService.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT_ERROR);
            log.info(STATUS_UPDATED);

            return ResponseEntity.ok(createFailureResponse(START_WORKFLOW_FAILURE, generateErrorDetails(response)));
        }
    }
}
