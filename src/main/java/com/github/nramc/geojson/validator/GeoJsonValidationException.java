package com.github.nramc.geojson.validator;

import java.util.Set;

/**
 * Exception thrown when a GeoJSON object fails validation.
 * <p>
 * This exception is used to indicate that a GeoJSON object contains validation errors,
 * and it provides details about these errors. The exception includes a message
 * describing the issue and a set of {@link ValidationError} objects with specific error details.
 * </p>
 *
 * <p>Example usage:
 * <pre>{@code
 * Set<ValidationError> errors = new HashSet<>();
 * errors.add(new ValidationError("coordinates", "Coordinates are invalid", "coordinates.invalid"));
 * throw new GeoJsonValidationException("GeoJSON validation failed", errors);
 * }</pre></p>
 */
public class GeoJsonValidationException extends RuntimeException {
    private final Set<ValidationError> errors;

    /**
     * Constructs a new {@code GeoJsonValidationException} with the specified message and errors.
     *
     * @param message A description of the validation failure. Must not be null.
     * @param errors  A set of {@link ValidationError} objects describing the specific validation errors. Must not be null or empty.
     */
    public GeoJsonValidationException(String message, Set<ValidationError> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Returns the set of validation errors that caused this exception.
     *
     * @return A set of {@link ValidationError} objects. Never null, but may be empty.
     */
    public Set<ValidationError> getErrors() {
        return errors;
    }
}