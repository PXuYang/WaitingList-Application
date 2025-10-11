package com.sean.restaurant.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException e, HttpServletRequest req) {
        String msg = "Duplicate value violates a unique constraint.";
        var cause = e.getMostSpecificCause();
        if (cause != null && cause.getMessage() != null &&
            cause.getMessage().toLowerCase().contains("unique")) {
            msg = "Table label already exists.";
        }
        ApiError body = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                msg,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .findFirst().orElse("Validation failed.");
        ApiError body = new ApiError(400, "Bad Request", msg, req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadArg(IllegalArgumentException e, HttpServletRequest req) {
        ApiError body = new ApiError(400, "Bad Request", e.getMessage(), req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException e, HttpServletRequest req) {
        ApiError body = new ApiError(404, "Not Found", "Resource not found.", req.getRequestURI(), Instant.now());
        return ResponseEntity.status(404).body(body);
    }
}

