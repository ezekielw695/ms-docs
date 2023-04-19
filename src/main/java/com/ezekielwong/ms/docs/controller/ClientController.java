package com.ezekielwong.ms.docs.controller;

import com.ezekielwong.ms.docs.domain.request.client.ClientWorkflowRequest;
import com.ezekielwong.ms.docs.domain.response.StandardResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.WorkflowResponse;
import com.ezekielwong.ms.docs.service.impl.ClientServiceImpl;
import com.ezekielwong.ms.docs.service.impl.WebClientServiceImpl;
import com.ezekielwong.ms.docs.utils.DateTimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    private final ClientServiceImpl clientServiceImpl;

    private final WebClientServiceImpl webClientServiceImpl;

    private final DateTimeUtils dateTimeUtils;

    @PostMapping("/workflow/start")
    public ResponseEntity<StandardResponse<Object>> startWorkflow (@RequestBody @Validated ClientWorkflowRequest clientRequest)
        throws ParserConfigurationException, IllegalAccessException, TransformerException,
            NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        LocalDateTime startTime = LocalDateTime.now();
        log.debug("### Start Workflow began at: {} ###", dateTimeUtils.getLocalDateTime(startTime));

        String caseId = clientRequest.getCaseId();
        setBaseRequest(clientRequest, httpServletRequest);

        // Generate XML string from client workflow request JSON object
        String params = clientServiceImpl.generateXmlString(clientRequest);
        log.info("Client workflow JSON object converted into XML string");

        // Save/update client workflow request
        clientServiceImpl.saveOrUpdate(clientRequest);
        log.info("Client request saved/updated");

        // Send client workflow request to third party app
        Object response = webClientServiceImpl.sendWorkflow(caseId, clientRequest.getName(), params);
        log.info("Workflow response received from third party app");

        // Success response with workflow details
        if (response.getClass() == WorkflowResponse.class) {

            // Update status to TPA_REQ_SENT
            clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT);
            log.info(STATUS_UPDATED);

            LocalDateTime endTime = LocalDateTime.now();
            log.debug("### Start Workflow ended at: {} ###", dateTimeUtils.getLocalDateTime(endTime));
            log.info("### REQUEST TOOK {} SECONDS ###", dateTimeUtils.getTimeTaken(startTime, endTime));

            return ResponseEntity.ok(createSuccessResponse(response, START_WORKFLOW_SUCCESS));

        // Failure response with error details
        } else {

            // Update status to TPA_REQ_SENT_ERROR
            clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT_ERROR);
            log.info(STATUS_UPDATED);

            return ResponseEntity.ok(createFailureResponse(START_WORKFLOW_FAILURE, generateErrorDetails(response)));
        }
    }
}
