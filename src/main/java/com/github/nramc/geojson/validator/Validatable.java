package com.github.nramc.geojson.validator;

public interface Validatable {
    boolean isValid();

    ValidationResult validate();
}
