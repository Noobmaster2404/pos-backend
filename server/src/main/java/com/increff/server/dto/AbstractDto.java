package com.increff.server.dto;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Objects;

import com.increff.commons.exception.ApiException;
import com.increff.commons.model.ClientForm;

public abstract class AbstractDto {
    
    protected void normalize(ClientForm form) throws RuntimeException {
        try {
            for (Field field : form.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (Objects.equals(field.getType(), String.class)) {
                    String value = (String) field.get(form);
                    if (Objects.nonNull(value)) {
                        field.set(form, value.trim().toLowerCase());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error during normalization", e);
        }
    }

    protected void validate(ClientForm form) throws ApiException {
        try {
            for (Field field : form.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(NotNull.class)) {
                    Object value = field.get(form);
                    if (Objects.isNull(value)) {
                        throw new ApiException(getPrefix() + field.getName() + " cannot be null");
                    }
                    if (value instanceof String) {
                        String strValue = (String) value;
                        if (strValue.isEmpty()) {
                            throw new ApiException(getPrefix() + field.getName() + " cannot be empty");
                        }
                        
                        // Phone number validation
                        if (field.getName().toLowerCase().contains("phone")) {
                            if (!strValue.matches("\\d{10}")) {
                                throw new ApiException(getPrefix() + field.getName() + " must be exactly 10 digits");
                            }
                        }
                        
                        // Email validation
                        if (field.getName().toLowerCase().contains("email")) {
                            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
                            if (!strValue.matches(emailRegex)) {
                                throw new ApiException(getPrefix() + field.getName() + " must be a valid email address");
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error during validation", e);
        }
    }

    // Each DTO must define its prefix so that error messages are easier to understand
    protected abstract String getPrefix();
}
