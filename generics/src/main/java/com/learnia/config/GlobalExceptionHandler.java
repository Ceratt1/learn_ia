package com.learnia.config;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String DEFAULT_BAD_REQUEST_MESSAGE = "Invalid request input";
    private static final String DEFAULT_INTERNAL_ERROR_MESSAGE = "Unexpected server error";

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Object> handleBindException(WebExchangeBindException ex, ServerWebExchange exchange) {
        return buildBadRequestValidationResponse(ex.getBindingResult().getFieldErrors(), exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        return buildBadRequestValidationResponse(ex.getBindingResult().getFieldErrors(), extractPath(request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> fieldErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> lastPathSegment(violation),
                        violation -> violation.getMessage() == null ? "invalid value" : violation.getMessage(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                extractPath(request),
                fieldErrors);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ServerWebInputException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        String message = ex instanceof ServerWebInputException input && input.getReason() != null
                ? input.getReason()
                : ex.getMessage();
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                defaultIfBlank(message, DEFAULT_BAD_REQUEST_MESSAGE),
                extractPath(request),
                Collections.emptyMap());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception ex, WebRequest request) {
        log.error("Unhandled exception caught by global handler", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                DEFAULT_INTERNAL_ERROR_MESSAGE,
                extractPath(request),
                Collections.emptyMap());
    }

    private ResponseEntity<Object> buildBadRequestValidationResponse(
            Iterable<FieldError> errors,
            String path) {
        Map<String, String> fieldErrors = toFieldErrorMap(errors);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", path, fieldErrors);
    }

    private Map<String, String> toFieldErrorMap(Iterable<FieldError> errors) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : errors) {
            fieldErrors.putIfAbsent(
                    fieldError.getField(),
                    defaultIfBlank(fieldError.getDefaultMessage(), "invalid value"));
        }
        return fieldErrors;
    }

    private ResponseEntity<Object> buildResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> fieldErrors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        if (!fieldErrors.isEmpty()) {
            body.put("fieldErrors", fieldErrors);
        }

        return ResponseEntity.status(status).body(body);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    private String lastPathSegment(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
