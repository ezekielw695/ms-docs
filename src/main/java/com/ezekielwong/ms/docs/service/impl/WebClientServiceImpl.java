package com.ezekielwong.ms.docs.service.impl;

import com.ezekielwong.ms.docs.domain.request.ms.WorkflowRequest;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.AccessTokenErrorResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.ThirdPartyAppErrorResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.WorkflowResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.AccessTokenResponse;
import com.ezekielwong.ms.docs.exception.ThirdPartyAppCallErrorException;
import com.ezekielwong.ms.docs.exception.common.InvalidJwtException;
import com.ezekielwong.ms.docs.exception.ThirdPartyAppNullResponseException;
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

import static com.ezekielwong.ms.docs.constant.AppConstants.*;
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

    private final WebClient webClient;

    private final JwtUtils jwtUtils;

    private final ClientServiceImpl clientServiceImpl;

    /**
     * Third party app endpoint detail: JWT grant type
     */
    @Value("${jwt.grant-type}")
    private String grantType;

    /**
     * Third party app endpoint detail: URI to request access token
     */
    @Value("${request.access.token.url}")
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
    public Object sendWorkflow(String caseId, String name, String params)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        log.debug("Sending workflow request to third party app");
        WorkflowRequest workflowRequest = WorkflowRequest.builder()
                .name(name)
                .params(params)
                .build();

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
                .doOnError(throwable -> handleCallError(caseId, throwable))
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
        log.debug("New JSON web token generated: \n{}", jwt);

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
                .doOnError(throwable -> handleCallError(caseId, throwable))
                .block();

        // Response is null
        if (response == null) {

            log.error(NULL_RESPONSE);

            // Update status to TPA_REQ_SENT_ERROR
            clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT_ERROR);
            log.info(STATUS_UPDATED);

            throw new ThirdPartyAppNullResponseException(UNKNOWN_ERROR, "Third party app returned null access token");

        // Invalid JSON web token
        } else if (response.getClass() == AccessTokenErrorResponse.class) {

            log.error(INVALID_JWT_EXCEPTION_MSG);

            // Update status to TPA_REQ_SENT_ERROR
            clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_SENT_ERROR);
            log.info(STATUS_UPDATED);

            AccessTokenErrorResponse tokenError = (AccessTokenErrorResponse) response;
            String errMsg = INVALID_JWT_EXCEPTION_MSG + String.format(": [ error: \"%s\", errorDesc: \"%s\" ]",
                    tokenError.getError(), tokenError.getErrorDesc());

            throw new InvalidJwtException(INVALID_JWT_EXCEPTION, errMsg);
        }

        // Successful request containing third party app access token
        AccessTokenResponse tokenResponse = (AccessTokenResponse) response;
        savedAccessToken = tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken();
        log.debug("Getting new access token: \n{}", savedAccessToken);

        return savedAccessToken;
    }

    /**
     * Handle third party app call error
     *
     * @param caseId Unique client workflow request case identifier
     * @param throwable Error/Exception thrown calling the third party app
     */
    private void handleCallError(String caseId, Throwable throwable) {

        log.error(THIRD_PARTY_APP_CALL_ERROR_MSG);

        // Update status to TPA_REQ_NOT_SENT
        clientServiceImpl.updateStatus(caseId, THIRD_PARTY_APP_REQUEST_NOT_SENT);
        log.info(STATUS_UPDATED);

        Throwable cause = throwable.getCause();
        String errMsg = THIRD_PARTY_APP_CALL_ERROR_MSG + ": [ " + (cause != null ? cause.getMessage() : throwable.getMessage()) + " ]";

        throw new ThirdPartyAppCallErrorException(THIRD_PARTY_APP_CALL_ERROR, errMsg);
    }
}
