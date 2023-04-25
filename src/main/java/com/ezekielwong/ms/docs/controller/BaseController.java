package com.ezekielwong.ms.docs.controller;

import com.ezekielwong.ms.docs.domain.request.BaseApiRequest;
import com.ezekielwong.ms.docs.domain.response.StandardResponse;
import com.ezekielwong.ms.docs.domain.response.thirdpartyapp.error.ThirdPartyAppErrorResponse;
import com.ezekielwong.ms.docs.error.ErrorInfo;
import com.ezekielwong.ms.docs.error.ErrorResponse;
import com.ezekielwong.ms.docs.error.ProviderError;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.filenet.response.FilenetResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

import static com.ezekielwong.ms.docs.constant.Constants.*;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.UNKNOWN_ERROR;
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

        StandardResponse<T> response = new StandardResponse<>();
        response.setData(t);
        response.setMessage(message);
        response.setStatus(SUCCESS);

        return response;
    }

    protected <T> StandardResponse<T> createFailureResponse(String message, ErrorResponse errorResponse) {

        StandardResponse<T> response = new StandardResponse<>();
        response.setMessage(message);
        response.setStatus(FAILURE);
        response.setErrorResponse(errorResponse);

        return response;
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
                List<ProviderError> providerErrorList = new ArrayList<>();

                for (ThirdPartyAppErrorResponse.ValidationError error : errorResponse.getValidationErrorList()) {
                    log.error("Validation error: " + error.toString());
                    ProviderError providerError = new ProviderError();
                    providerError.setProviderErrorCode(error.getErrorCode().toString());
                    providerError.setProviderErrorDetail(error.getErrorMessage());

                    providerErrorList.add(providerError);
                }

                errorDetail.setProviderErrorList(providerErrorList);
            }

            return new ErrorResponse(null, null, new ErrorInfo(Collections.singletonList(errorDetail)));

        // Unknown response
        } else {
            log.error(UNKNOWN_RESPONSE);
            return new ErrorResponse(new GenericBadException(UNKNOWN_ERROR, response.toString()));
        }
    }

    protected List<String> getExceptionMsgList(MethodArgumentNotValidException exception) {

        log.error("Request validation error");
        List<String> exceptionMsgList = new ArrayList<>();

        // Request field errors
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            String errMsg = error.getField() + ": " + error.getDefaultMessage();
            log.error(errMsg);

            exceptionMsgList.add(errMsg);
        }

        return exceptionMsgList;
    }
}
