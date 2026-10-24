package com.rama.fest.ramafest.config;

import java.util.Arrays;

public enum AppConstants {

    REGISTRATION_OPEN("registration_open"),
    REGISTRATION_CLOSED("registration_closed"),
    COMPLETED("completed");

    private final String value;

    AppConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AppConstants fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Event status is required");
        }

        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value)
                        || status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported event status: " + value));
    }

}
