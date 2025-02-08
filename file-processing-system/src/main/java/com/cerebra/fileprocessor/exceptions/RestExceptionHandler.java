package com.cerebra.fileprocessor.exceptions;

import com.cerebra.fileprocessor.response.RestApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class.getName());
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<RestApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                                   WebRequest request) {

        LOGGER.error(ANSI_RED +"HttpMessageNotReadableException  : {}"+ ANSI_RESET, ex.getMessage(), ex);
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message("Invalid Inputs")
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex,
                                                                                      WebRequest request) {
        LOGGER.error(ANSI_RED +"ConstraintViolationException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            errors.add(cv.getMessage());
        }
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(errors)
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex,
                                                                                 WebRequest request) {

        LOGGER.error(ANSI_RED +"AccessDeniedException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(ex.getMessage())
                .errors(request.getDescription(false)).build(), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<RestApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                                   WebRequest request) {

        LOGGER.error(ANSI_RED +"MethodArgumentNotValidException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        Collections.sort(errors);

        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(errors)
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<RestApiResponse<Object>> getRestApiException(RestApiException ex, WebRequest request) {

        LOGGER.error(ANSI_RED +"RestApiException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(ex.getErrorMessage())
                .errors(request.getDescription(false)).build(), ex.getHttpStatus());

    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestApiResponse<Object>> getResponseStatusException(ResponseStatusException ex,
                                                                              WebRequest request) {

        LOGGER.error(ANSI_RED +"ResponseStatusException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(ex.getMessage())
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestApiResponse<Object>> getBadCredentialsExceptionStatus(BadCredentialsException ex, WebRequest request) {

        LOGGER.error(ANSI_RED +"BadCredentialsException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(ex.getMessage())
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponse<Object>> getExceptionStatus(Exception ex, WebRequest request) {

        LOGGER.error(ANSI_RED +"Exception :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message("Internal Service Error")
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestApiResponse<Object>> getDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {

        LOGGER.error(ANSI_RED +"DataIntegrityViolationException :  {}"+ ANSI_RESET, ex.getMessage(), ex);
        String errorMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        return new ResponseEntity<>(RestApiResponse.builder().timestamp(new Date()).message(errorMessage)
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);
    }

}