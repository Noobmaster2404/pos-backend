package com.increff.commons.exception;

import com.increff.commons.model.FieldErrorData;
import java.util.ArrayList;
import java.util.List;

public class ApiException extends Exception {
    private final List<FieldErrorData> errors;

    public ApiException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public ApiException(String message, List<FieldErrorData> errors) {
        super(message);
        this.errors = errors;
    }

    public List<FieldErrorData> getErrors() {
        return errors;
    }
}

