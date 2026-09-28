package com.SmartMov.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<Map<String, Object>> handleResponseStatusException(
                        ResponseStatusException exception) {

                Map<String, Object> error = new HashMap<>();
                error.put("status", exception.getStatusCode().value());
                error.put("error", exception.getReason());

                return ResponseEntity
                                .status(exception.getStatusCode())
                                .body(error);
        }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(BusinessException.class)
public ResponseEntity<Map<String, String>> handleBusinessException(
        BusinessException exception) {

    Map<String, String> error = new HashMap<>();

    error.put("error", exception.getMessage());

    return ResponseEntity.badRequest().body(error);
}

@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<Map<String, String>> handleInvalidRequestBody(
        HttpMessageNotReadableException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", "Invalid request body");

    return ResponseEntity.badRequest().body(error);
}

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<Map<String, String>> handleMissingParameter(
                        MissingServletRequestParameterException exception) {

                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing form field: " + exception.getParameterName());

                return ResponseEntity.badRequest().body(error);
        }

        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<Map<String, String>> handleMissingPart(
                        MissingServletRequestPartException exception) {

                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing file part: " + exception.getRequestPartName());

                return ResponseEntity.badRequest().body(error);
        }

        @ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, String>> handleUnexpectedException(
        Exception exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", "Internal server error");

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
}
}