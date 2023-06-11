package com.ezekielwong.ms.docs.advice;

import com.ezekielwong.ms.docs.controller.BaseController;
import com.ezekielwong.ms.docs.controller.ClientController;
import com.ezekielwong.ms.docs.domain.response.ms.StandardResponse;
import com.ezekielwong.ms.docs.error.ErrorResponse;
import com.ezekielwong.ms.docs.exception.InvalidJwtException;
import com.ezekielwong.ms.docs.exception.ThirdPartyAppCallErrorException;
import com.ezekielwong.ms.docs.exception.ThirdPartyAppNullResponseException;
import com.ezekielwong.ms.docs.exception.common.BaseException;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.exception.common.GenericException;
import com.ezekielwong.ms.docs.exception.common.GenericSuccessException;
import org.postgresql.util.PSQLException;
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

import static com.ezekielwong.ms.docs.constant.AppConstants.START_WORKFLOW_FAILURE;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.MSDB_PSQL_EXCEPTION;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.UNKNOWN_ERROR;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.MSDB_PSQL_EXCEPTION_MSG;

/**
 * Exception handler for exceptions thrown by ClientController
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ClientController.class)
public class ClientControllerAdvice extends BaseController {

    /**
     * Client payload content is not valid after business rule checking
     *
     * @param exception {@link GenericSuccessException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(GenericSuccessException.class)
    public StandardResponse<Object> handleGenericSuccessException(GenericSuccessException exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, new ErrorResponse(exception));
    }

    /**
     * Client request fails to fulfill the required payload attributes
     *
     * @param exception {@link MethodArgumentNotValidException}, {@link GenericBadException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            GenericBadException.class
    })
    public StandardResponse<Object> handleBadRequestException(Exception exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, getBadRequestErrorResponse(exception));
    }

    /**
     * An invalid JSON web token was used to call the third party app
     *
     * @param exception {@link InvalidJwtException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidJwtException.class)
    public StandardResponse<Object> handleInvalidJwtException(InvalidJwtException exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, new ErrorResponse(exception));
    }

    /**
     * MS encounters PSQL exception
     *
     * @param exception {@link PSQLException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PSQLException.class)
    public StandardResponse<Object> handlePSQLException(PSQLException exception) {
        
        String errMsg = MSDB_PSQL_EXCEPTION_MSG + String.format(": [ %s ]", getThrowableCause(exception));
        ErrorResponse errorResponse = new ErrorResponse(new BaseException(MSDB_PSQL_EXCEPTION, errMsg));

        return createFailureResponse(START_WORKFLOW_FAILURE, errorResponse);
    }

    /**
     * Client encounters third party app exception
     *
     * @param exception {@link ThirdPartyAppCallErrorException}, {@link ThirdPartyAppNullResponseException}
     * @return {@link StandardResponse} with failure details
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            ThirdPartyAppCallErrorException.class,
            ThirdPartyAppNullResponseException.class
    })
    public StandardResponse<Object> handleThirdPartyAppException(GenericException exception) {
        return createFailureResponse(START_WORKFLOW_FAILURE, new ErrorResponse(exception));
    }

    /**
     * Client encounters checked exception
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
        ErrorResponse errorResponse = new ErrorResponse(new BaseException(UNKNOWN_ERROR, errMsg));

        return createFailureResponse(START_WORKFLOW_FAILURE, errorResponse);
    }
}
