package com.increff.server.spring;

import com.increff.commons.exception.ApiException;
import com.increff.commons.model.FieldErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.transaction.TransactionSystemException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        if (Objects.nonNull(ex.getErrors()) && !ex.getErrors().isEmpty()) {
            response.put("errors", ex.getErrors());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        List<FieldErrorData> errors = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            FieldErrorData fieldError = new FieldErrorData();
            fieldError.setField(((FieldError) error).getField());
            fieldError.setMessage(error.getDefaultMessage());
            errors.add(fieldError);
        });
        
        response.put("message", "Validation failed");
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "An unexpected error occurred");
        
        FieldErrorData error = new FieldErrorData();
        error.setField("global");
        error.setMessage(ex.getMessage());
        
        List<FieldErrorData> errors = new ArrayList<>();
        errors.add(error);
        
        response.put("errors", errors);
        ex.printStackTrace(); // For debugging
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Object> handleTransactionException(TransactionSystemException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transaction failed");
        
        FieldErrorData error = new FieldErrorData();
        error.setField("global");
        error.setMessage(ex.getMostSpecificCause().getMessage());
        
        List<FieldErrorData> errors = new ArrayList<>();
        errors.add(error);
        
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 