package com.optimize.common.entities.controller;

import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.dto.ValidationErrorDTO;
import com.optimize.common.entities.util.Response;
import com.optimize.common.entities.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.optimize.common.entities.exception.ApplicationException;
import com.optimize.common.entities.exception.AuthenticationException;
import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.common.entities.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//@ControllerAdvice
@Slf4j(topic = "EventLog")
public class CommonAdviceController extends ResponseEntityExceptionHandler {
    protected final CustomMessageSource messageSource;
    private static final String ERROR = "ERROR:  ===>";

    public CommonAdviceController(CustomMessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @ExceptionHandler(value
            =  ResourceNotFoundException.class )
    protected ResponseEntity<Response> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());
        logger(ex);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.NOT_FOUND, bodyOfResponse, ex.getService()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value
            =  AuthenticationException.class )
    protected ResponseEntity<Response> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());
        logger(ex);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.UNAUTHORIZED, bodyOfResponse, ex.getService()), HttpStatus.UNAUTHORIZED);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadableExceptionOverride (
            HttpMessageNotReadableException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());
        logger(ex);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.UNAUTHORIZED, bodyOfResponse, ""), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleHttpMessageNotReadableExceptionOverride(ex, request);
    }

    @ExceptionHandler(value
            =  ApplicationException.class )
    protected ResponseEntity<Response> handleApplicationException(
            ApplicationException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());
        logger(ex);
        return new ResponseEntity<>(Response
                .errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, bodyOfResponse, ex.getService()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value
            =  CustomValidationException.class )
    protected ResponseEntity<Response> handleCustomValidationException(
            CustomValidationException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());
        logger(ex);
        return new ResponseEntity<>(Response
                .errorResponse(HttpStatus.BAD_REQUEST, bodyOfResponse, ex.getService()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value
            =  Exception.class )
    public ResponseEntity<Response> handleException(Exception ex) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());

        if (ex instanceof ConstraintViolationException || ex instanceof DataIntegrityViolationException) {
            bodyOfResponse = "ERROR : VIOLATION DE CONTRAINTE D'UNICITE" + ex.getCause().getLocalizedMessage();
            if (ex instanceof DataIntegrityViolationException dve) {
                final String message = dve.getMostSpecificCause().getMessage();
                final int open = message.lastIndexOf("(") + 1;
                final int close = message.lastIndexOf(")");
                final int length = message.length();
                String constraintValue = message.substring(open, close);
                constraintValue += " " + message.substring(close + 1);
                bodyOfResponse = "VIOLATION DE CONTRAINTE D'INTEGRITE : " +  constraintValue;
            }
            return new ResponseEntity<>(ResponseUtil
                    .errorResponse(HttpStatus.CONFLICT, bodyOfResponse),
                    HttpStatus.CONFLICT);
        }

        logger(ex);


        if (ex instanceof JpaSystemException jpaEx) {
            bodyOfResponse = jpaEx.getMostSpecificCause().getMessage();
            log.error(Arrays.toString(jpaEx.getStackTrace()));
            return new ResponseEntity<>(ResponseUtil
                    .errorResponse(HttpStatus.UNAUTHORIZED, bodyOfResponse),
                    HttpStatus.UNAUTHORIZED);
        }

        if (ex instanceof ClientAbortException caEx) {
            bodyOfResponse = caEx.getMessage();
            log.error("CLIENT ABORT EXCEPTION  ===> {}",caEx.toString());
        }

        return new ResponseEntity<>(ResponseUtil
                .errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, bodyOfResponse),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return customHandleMethodArgumentNotValid(ex, headers, status, request);
    }

    protected ResponseEntity<Object> customHandleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ValidationErrorDTO(fieldName, this.messageSource.getMessage(errorMessage)).toString());
        });
        logger(ex);
        log.error(ERROR+errors);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.BAD_REQUEST, errors, ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value
            =  NullPointerException.class )
    protected ResponseEntity<Response> handleNullPoiException(
            NullPointerException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getMessage());
        logger(ex);
        return new ResponseEntity<>(ResponseUtil
                .errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, bodyOfResponse),
                HttpStatus.OK);
    }

    @ExceptionHandler(value
            =  ConstraintViolationException.class )
    protected ResponseEntity<Response> constraintException(
            ConstraintViolationException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getLocalizedMessage());
        logger(ex);
        return new ResponseEntity<>(ResponseUtil
                .errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, bodyOfResponse),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value
            =  DataIntegrityViolationException.class )
    protected ResponseEntity<Response> dataIntegrityException(
            DataIntegrityViolationException ex, WebRequest request) {
        String bodyOfResponse = this.messageSource.getMessage(ex.getLocalizedMessage());
        logger(ex);
        return new ResponseEntity<>(ResponseUtil
                .errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, bodyOfResponse),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        String bodyOfResponse = this.messageSource.getMessage(error);
        logger(ex);
        log.error(ERROR+error);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.BAD_REQUEST, bodyOfResponse, ""), HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        String bodyOfResponse = this.messageSource.getMessage(ex.getLocalizedMessage());
        logger(ex);
        log.error(ERROR+error);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.NOT_FOUND, bodyOfResponse, ""), HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(
                " method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));
        String bodyOfResponse = this.messageSource.getMessage(ex.getLocalizedMessage());
        logger(ex);
        log.error(ERROR+builder.toString());
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.METHOD_NOT_ALLOWED, bodyOfResponse, ""),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        String bodyOfResponse = this.messageSource.getMessage(builder.toString());
        logger(ex);
        return new ResponseEntity<>(Response.errorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, bodyOfResponse, ""), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    private void logger(Exception ex) {
        ex.printStackTrace();
        log.error("===> ERROR : ", ex);
        log.error("===> Error Message: {}", this.messageSource.getMessage(ex.getMessage()));
    }
}
