package com.github.nramc.geojson.validator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An interface for objects that can be validated. It provides methods to check
 * if the object is valid and to retrieve validation results.
 *
 * <p>Implementing classes must define the {@code validate()} method, which performs
 * the validation logic and returns a {@code ValidationResult} object containing
 * any errors found.</p>
 *
 * @see ValidationResult
 * @see ValidationError
 */
public interface Validatable {

    /**
     * Checks if the GeoJson is valid. GeoJson is considered valid if there are no validation errors present.
     *
     * @return {@code true} if the object is valid (i.e., there are no validation errors), otherwise {@code false}.
     */
    @JsonIgnore
    default boolean isValid() {
        return !validate().hasErrors();
    }

    /**
     * Checks if the object has any validation errors.
     *
     * @return {@code true} if the object has validation errors, otherwise {@code false}.
     */
    @JsonIgnore
    default boolean hasErrors() {
        return validate().hasErrors();
    }

    /**
     * Validates the GeoJson and returns the results of the validation.
     *
     * @return A {@code ValidationResult} object containing any errors found during the validation process.
     * If there are no errors, the {@code ValidationResult} will contain an empty set of errors.
     * @see ValidationResult
     * @see ValidationError
     */
    ValidationResult validate();
}
