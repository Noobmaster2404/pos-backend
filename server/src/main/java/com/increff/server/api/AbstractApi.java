package com.increff.server.api;

import com.increff.commons.exception.ApiException;
import java.lang.reflect.Field; 
import javax.persistence.Column;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Objects;

public class AbstractApi {
    protected void checkValid(Object object) throws ApiException {
        if (Objects.isNull(object)) {
            throw new ApiException("Object cannot be null");
        }

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                
                // Check @Column constraints
                Column column = field.getAnnotation(Column.class);
                if (Objects.nonNull(column)) {
                    if (!column.nullable() && Objects.isNull(value)) {
                        throw new ApiException("Field '" + field.getName() + "' cannot be null");
                    }
                    
                    if (value instanceof String && column.length() > 0) {
                        String strValue = (String) value;
                        if (strValue.length() > column.length()) {
                            throw new ApiException("Field '" + field.getName() + "' exceeds maximum length of " + column.length());
                        }
                    }
                }

                Size size = field.getAnnotation(Size.class);
                if (Objects.nonNull(size) && Objects.nonNull(value)) {
                    if (value instanceof String) {
                        String strValue = (String) value;
                        if (strValue.length() < size.min() || strValue.length() > size.max()) {
                            throw new ApiException("Field '" + field.getName() + "' length must be between " + size.min() + " and " + size.max());
                        }
                    } else if (value instanceof Collection) {
                        Collection<?> collection = (Collection<?>) value;
                        if (collection.size() < size.min() || collection.size() > size.max()) {
                            throw new ApiException("Collection '" + field.getName() + "' size must be between " + size.min() + " and " + size.max());
                        }
                    }
                }

                Pattern pattern = field.getAnnotation(Pattern.class);
                if (Objects.nonNull(pattern) && value instanceof String) {
                    String strValue = (String) value;
                    if (!strValue.matches(pattern.regexp())) {
                        throw new ApiException("Field '" + field.getName() + "' does not match required pattern");
                    }
                }

            } catch (IllegalAccessException e) {
                throw new ApiException("Error accessing field: " + field.getName());
            }
        }
    }
}
