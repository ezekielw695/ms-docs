package com.ezekielwong.ms.docs.service.impl;

import com.ezekielwong.ms.docs.domain.request.ms.WorkflowRequest;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.AccessTokenResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.WorkflowResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.error.AccessTokenErrorResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.error.ThirdPartyAppErrorResponse;
import com.ezekielwong.ms.docs.exception.callerror.ThirdPartyAppCallErrorException;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.exception.ms.InvalidJwtException;
import com.ezekielwong.ms.docs.service.WebClientService;
import com.ezekielwong.ms.docs.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

import static com.ezekielwong.ms.docs.constant.Constants.*;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.*;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.INVALID_JWT_EXCEPTION_MSG;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.THIRD_PARTY_APP_CALL_ERROR_MSG;

/**
 * Service for WebClient calls
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {

    private final JwtUtils jwtUtils;

    private final ClientServiceImpl clientServiceImpl;

    private final WebClient webClient;

    /**
     * Third party app endpoint detail: JWT grant type
     */
    @Value("${jwt.grant-type}")
    private String grantType;

    /**
     * Third party app endpoint detail: URI to request access token
     */
    @Value("${request.access-token.url}")
    private String requestAccessTokenUrl;

    /**
     * Third party app endpoint detail: URI to start workflow
     */
    @Value("${start.workflow.url}")
    private String startWorkflowUrl;

    /**
     * Previously saved third party app access token
     */
    private String savedAccessToken;

    /**
     * Send client workflow request to third party app
     *
     * @param caseId Unique client workflow request case identifier
     * @param name Name of the workflow
     * @param params XML data used when starting the workflow
     * @return {@link WorkflowResponse} (successful) or <br>
     *          {@link ThirdPartyAppErrorResponse} (unsuccessful)
     * @throws NoSuchAlgorithmException Requested cryptographic algorithm is not available
     * @throws IOException I/O operation interrupted or failed
     * @throws InvalidKeySpecException Key specification is invalid
     */
    @Override
    public Object sendWorkflow(String caseId, String name, String params) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        // Create workflow request
        log.debug("Sending workflow request to third party app");
        WorkflowRequest workflowRequest = new WorkflowRequest(name, params);

        String auth = getAccessToken(caseId);

        return webClient
                .post()
                .uri(startWorkflowUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, auth)
                .bodyValue(workflowRequest)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(ThirdPartyAppErrorResponse.class);
                    } else {
                        return clientResponse.bodyToMono(WorkflowResponse.class);
                    }
                })
                .doOnError(throwable -> {
                    log.error(THIRD_PARTY_APP_CALL_ERROR_MSG);

                    // Update status to TPA_REQ_NOT_SENT
                    clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_NOT_SENT);
                    log.info(STATUS_UPDATED);

                    String errMsg = THIRD_PARTY_APP_CALL_ERROR_MSG + ": [ " + throwable.getMessage() + " ]";
                    throw new ThirdPartyAppCallErrorException(THIRD_PARTY_APP_CALL_ERROR, errMsg);
                })
                .block();
    }

    /**
     * Get third party app access token
     *
     * @param caseId Unique client workflow request case identifier
     * @return Third party app access token
     * @throws NoSuchAlgorithmException Requested cryptographic algorithm is not available
     * @throws IOException I/O operation interrupted or failed
     * @throws InvalidKeySpecException Key specification is invalid
     */
    private String getAccessToken(String caseId) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        long iat = Instant.now().getEpochSecond();
        Instant now = Instant.ofEpochSecond(iat);

        // Check for unexpired access token
        if (jwtUtils.accessTokenIsNotExpired(now, savedAccessToken).equals(Boolean.TRUE)) {
            log.debug("Getting unexpired access token: \n{}", savedAccessToken);
            return savedAccessToken;
        }

        // Generate new JSON web token
        String jwt = jwtUtils.generateWebToken(iat, now);
        log.debug("JWT generated: \n{}", jwt);

        Object response = webClient
                .post()
                .uri(requestAccessTokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", grantType).with("assertion", jwt))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(AccessTokenErrorResponse.class);
                    } else {
                        return clientResponse.bodyToMono(AccessTokenResponse.class);
                    }
                })
                .doOnError(throwable -> {
                    log.error(THIRD_PARTY_APP_CALL_ERROR_MSG);

                    // Update status to TPA_REQ_NOT_SENT
                    clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_NOT_SENT);
                    log.info(STATUS_UPDATED);

                    String errMsg = THIRD_PARTY_APP_CALL_ERROR_MSG + ": [ " + throwable.getMessage() + " ]";
                    throw new ThirdPartyAppCallErrorException(THIRD_PARTY_APP_CALL_ERROR, errMsg);
                })
                .block();

        // Response is null
        if (response == null) {
            log.error(NULL_RESPONSE);

            // Update status to TPA_REQ_SENT_ERROR
            clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT_ERROR);
            log.info(STATUS_UPDATED);

            throw new GenericBadException(UNKNOWN_ERROR, "Third party app returned null access token");

        // Invalid JSON web token
        } else if (response.getClass() == AccessTokenErrorResponse.class) {
            log.error(INVALID_JWT_EXCEPTION_MSG);

            // Update status to TPA_REQ_SENT_ERROR
            clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT_ERROR);
            log.info(STATUS_UPDATED);

            AccessTokenErrorResponse tokenError = (AccessTokenErrorResponse) response;
            String errMsg = INVALID_JWT_EXCEPTION_MSG + String.format(": [ error: \"%s\", error_desc: \"%s\" ]",
                    tokenError.getError(), tokenError.getErrorDesc());
            throw new InvalidJwtException(INVALID_JWT_EXCEPTION, errMsg);
        }

        // Successful request containing third party app access token
        AccessTokenResponse tokenResponse = (AccessTokenResponse) response;
        savedAccessToken = tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken();
        log.debug("Getting new access token: \n{}", savedAccessToken);

        return savedAccessToken;
    }
}