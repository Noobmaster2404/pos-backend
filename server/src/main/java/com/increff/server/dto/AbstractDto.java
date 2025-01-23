package com.increff.server.dto;

import com.increff.commons.exception.ApiException;
import com.increff.commons.model.FieldErrorData;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractDto {

    private final Validator validator;

    protected AbstractDto() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    protected void normalize(Object form) throws ApiException {
        try {
            for (Field field : form.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    String value = (String) field.get(form);
                    if (value != null) {
                        field.set(form, value.trim().toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            throw new ApiException("Error during normalization: " + e.getMessage());
        }
    }

    protected <T> void checkValid(T form) throws ApiException {
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        if (!violations.isEmpty()) {
            List<FieldErrorData> errorList = new ArrayList<>(violations.size());
            for (ConstraintViolation<T> violation : violations) {
                FieldErrorData error = new FieldErrorData();
                error.setField(violation.getPropertyPath().toString());
                error.setMessage(getPrefix() + violation.getMessage());
                errorList.add(error);
            }
            throw new ApiException("Validation failed", errorList);
        }
    }

    // Each DTO must define its prefix so that error messages are easier to understand
    protected abstract String getPrefix();
}
