package com.increff.server.dto;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class AbstractDto {

    protected void normalize(Object form) throws RuntimeException {
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

    // Each DTO must define its prefix so that error messages are easier to understand
    protected abstract String getPrefix();
}
