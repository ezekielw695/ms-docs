package com.ezekielwong.ms.docs.controller;

import com.ezekielwong.ms.docs.domain.request.BaseApiRequest;
import com.ezekielwong.ms.docs.domain.response.ms.StandardResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.ThirdPartyAppErrorResponse;
import com.ezekielwong.ms.docs.error.ErrorInfo;
import com.ezekielwong.ms.docs.error.ErrorResponse;
import com.ezekielwong.ms.docs.error.ProviderError;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.filenet.response.FilenetResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;
import java.util.stream.Collectors;

import static com.ezekielwong.ms.docs.constant.AppConstants.*;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.REQUEST_VALIDATION_ERROR;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.UNKNOWN_ERROR;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.REQUEST_VALIDATION_ERROR_MSG;
import static com.ezekielwong.ms.docs.constant.RequestHeaders.*;

@Slf4j
public abstract class BaseController {

    protected void setBaseRequest(BaseApiRequest request, HttpServletRequest httpServletRequest) {

        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            map.put(key, value);
        }

        log.debug(map.toString());

        request.setCorrelationId(map.get(X_CORRELATION_ID));
        request.setApiCountryCode(map.get(X_SOURCE_COUNTRY));
        request.setDateTime(map.get(X_SOURCE_DATE_TIME));
        request.setUpdatedChannel(map.get(X_SOURCE_ID));
    }

    protected <T> StandardResponse<T> createSuccessResponse(T t, String message) {

        return StandardResponse.<T>builder()
                .data(t)
                .message(message)
                .status(SUCCESS)
                .build();
    }

    protected <T> StandardResponse<T> createFailureResponse(String message, ErrorResponse errorResponse) {

        return StandardResponse.<T>builder()
                .message(message)
                .status(FAILURE)
                .errorResponse(errorResponse)
                .build();
    }

    protected ErrorResponse generateErrorDetails(Object response) {

        log.debug("Generating error details");
        ErrorInfo.ErrorDetail errorDetail = new ErrorInfo.ErrorDetail();

        // Filenet error response
        if (response.getClass() == FilenetResponse.class) {

            log.error("Filenet error");
            FilenetResponse errorResponse = (FilenetResponse) response;
            errorDetail.setErrorCode(errorResponse.getStatus().getCode());
            errorDetail.setErrorDescription(errorResponse.getStatus().getDesc());

            return new ErrorResponse(null, null, new ErrorInfo(Collections.singletonList(errorDetail)));

        // Third party app error response
        } else if (response.getClass() == ThirdPartyAppErrorResponse.class) {

            log.error("Third party app error");
            ThirdPartyAppErrorResponse errorResponse = (ThirdPartyAppErrorResponse) response;
            errorDetail.setErrorCode(errorResponse.getError().getErrorCode().toString());
            errorDetail.setErrorDescription(errorResponse.getError().getErrorMessage());

            // Check for additional validation error
            if (errorResponse.getValidationErrorList() != null) {

                List<ProviderError> providerErrorList = errorResponse.getValidationErrorList().stream()
                        .map(error -> ProviderError.builder()
                                .providerErrorCode(error.getErrorCode().toString())
                                .providerErrorDetail(error.getErrorMessage())
                                .build())
                        .collect(Collectors.toList());

                errorDetail.setProviderErrorList(providerErrorList);
            }

            return new ErrorResponse(null, null, new ErrorInfo(Collections.singletonList(errorDetail)));

        // Unknown response
        } else {

            log.error(UNKNOWN_RESPONSE);
            return new ErrorResponse(new GenericBadException(UNKNOWN_ERROR, response.toString()));
        }
    }

    protected ErrorResponse getBadRequestErrorResponse(Exception exception) {

        String errMsg = REQUEST_VALIDATION_ERROR_MSG;

        if (exception.getClass() == MethodArgumentNotValidException.class) {

            log.error("Request validation error");
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) exception;

            List<String> exceptionMsgList = new ArrayList<>();

            e.getBindingResult().getFieldErrors().forEach(error -> {

                String err = error.getField() + ": " + error.getDefaultMessage();
                log.error(err);

                exceptionMsgList.add(err);
            });

            errMsg += String.format(": %s", exceptionMsgList);

         } else {
            errMsg += String.format(": [ %s ]", getThrowableCause(exception));
        }

        return new ErrorResponse(new GenericBadException(REQUEST_VALIDATION_ERROR, errMsg));
    }

    protected String getThrowableCause(Exception exception) {

        Throwable cause = exception.getCause();
        String errMsg = (cause != null ? cause.getMessage() : exception.getMessage());
        log.error(errMsg);

        return errMsg;
    }
}
