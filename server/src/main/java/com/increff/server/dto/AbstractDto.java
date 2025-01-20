package com.increff.server.dto;

import com.increff.commons.exception.ApiException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Objects;

public abstract class AbstractDto {
    
    protected void normalize() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (Objects.equals(field.getType(), String.class)) {
                    String value = (String) field.get(this);
                    if (Objects.nonNull(value)) {
                        field.set(this, value.trim().toLowerCase());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error during normalization", e);
        }
    }

    protected void validate() throws ApiException {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(NotNull.class)) {
                    Object value = field.get(this);
                    if (Objects.isNull(value)) {
                        throw new ApiException(getPrefix() + field.getName() + " cannot be null");
                    }
                    if (value instanceof String && ((String) value).isEmpty()) {
                        throw new ApiException(getPrefix() + field.getName() + " cannot be empty");
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
