package com.github.nramc.geojson.validator;

import java.util.Set;

public class GeoJsonValidationException extends RuntimeException {
    private final Set<String> errors;

    public GeoJsonValidationException(String message, Set<String> errors) {
        super(message);
        this.errors = errors;
    }

    public Set<String> getErrors() {
        return errors;
    }
}
