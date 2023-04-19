package com.ezekielwong.ms.docs.advice;

import com.ezekielwong.ms.docs.controller.BaseController;
import com.ezekielwong.ms.docs.controller.ClientController;
import com.ezekielwong.ms.docs.domain.response.StandardResponse;
import com.ezekielwong.ms.docs.error.ErrorResponse;
import com.ezekielwong.ms.docs.exception.callerror.ThirdPartyAppCallErrorException;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.exception.ms.InvalidJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.ezekielwong.ms.docs.constant.Constants.*;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.*;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.*;

/**
 * Exception handler for exceptions thrown by ClientController
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ClientController.class)
public class ClientControllerAdvice extends BaseController {

    /**
     * Client payload content not valid after business rule checking
     *
     * @param exception {@link GenericBadException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(GenericBadException.class)
    public StandardResponse<Object> handleGenericBadException(GenericBadException exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, new ErrorResponse(exception));
    }

    /**
     * Client request validation error
     *
     * @param exception {@link MethodArgumentNotValidException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public StandardResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> exceptionMsgList = getExceptionMsgList(exception);
        String errMsg = REQUEST_VALIDATION_ERROR_MSG + String.format(": %s", exceptionMsgList.toString());
        ErrorResponse errorResponse = new ErrorResponse(new GenericBadException(REQUEST_VALIDATION_ERROR, errMsg));

        return createFailureResponse(START_WORKFLOW_FAILURE, errorResponse);
    }

    /**
     * An invalid JSON web token was used to call the third party app
     *
     * @param exception {@link InvalidJwtException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidJwtException.class)
    public StandardResponse<Object> handleInvalidJwtException(InvalidJwtException exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, new ErrorResponse(exception));
    }

    // TODO DB SQL EXCEPTION

    /**
     * Client is unable to call the third party app
     *
     * @param exception {@link ThirdPartyAppCallErrorException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ThirdPartyAppCallErrorException.class)
    public StandardResponse<Object> handleThirdPartyAppCallErrorException(ThirdPartyAppCallErrorException exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, new ErrorResponse(exception));
    }

    /**
     * Client encountered checked exception from XmlUtil or JwtUtil
     * <ul>
     *     <li>XmlUtil: ParserConfigurationException, IllegalAccessException, TransformerException</li>
     *     <li>JwtUtil: NoSuchAlgorithmException, IOException, InvalidKeyException</li>
     * </ul>
     *
     * @param exception {@link ParserConfigurationException}, {@link IllegalAccessException}, {@link TransformerException},
     *                  {@link NoSuchAlgorithmException}, {@link IOException}, {@link InvalidKeyException},
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            ParserConfigurationException.class,
            IllegalAccessException.class,
            TransformerException.class,
            NoSuchAlgorithmException.class,
            IOException.class,
            InvalidKeyException.class
    })
    public StandardResponse<Object> handleCheckedException(Exception exception) {
        String errMsg = String.format("[ %s ]", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(new GenericBadException(UNKNOWN_ERROR, errMsg));

        return createFailureResponse(START_WORKFLOW_FAILURE, errorResponse);
    }
}
