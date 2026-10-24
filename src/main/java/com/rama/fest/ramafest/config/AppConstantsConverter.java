package com.rama.fest.ramafest.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AppConstantsConverter implements AttributeConverter<AppConstants, String> {

    @Override
    public String convertToDatabaseColumn(AppConstants status) {
        return status == null ? null : status.getValue();
    }

    @Override
    public AppConstants convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return AppConstants.fromValue(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
