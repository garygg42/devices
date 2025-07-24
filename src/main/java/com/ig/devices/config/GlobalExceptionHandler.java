package com.ig.devices.config;

import com.ig.devices.exception.DeviceStateValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(getErrorsMap(errors));
    }

    @ExceptionHandler(DeviceStateValidationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidation(DeviceStateValidationException ex) {
        return ResponseEntity.badRequest().body(getErrorsMap(List.of(ex.getMessage())));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        var errorResponse = new HashMap<String, List<String>>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

}
