package com.github.nramc.geojson.validator;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a validation error that provides details about a specific validation failure.
 * <p>
 * This class encapsulates information about a validation error, including:
 * <ul>
 *     <li>The field that caused the error.</li>
 *     <li>An error message describing the nature of the validation failure.</li>
 *     <li>A key that can be used for identifying or localizing the error.</li>
 * </ul>
 * </p>
 *
 * <p>Example usage:
 * <pre>{@code
 * ValidationError error = ValidationError.of("coordinates", "coordinates must not be empty", "coordinates.invalid.empty");
 * }</pre></p>
 */
public class ValidationError implements Serializable {
    private final String field;
    private final String message;
    private final String key;

    /**
     * Constructs a new {@code ValidationError} with the specified field, message, and key.
     *
     * @param field   The name of the field that caused the validation error. Must not be null.
     * @param message A description of the validation error. Must not be null.
     * @param key     A key that identifies the type of validation error, useful for localization. Must not be null.
     */
    public ValidationError(String field, String message, String key) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(message);
        Objects.requireNonNull(key);
        this.field = field;
        this.message = message;
        this.key = key;
    }

    /**
     * Creates a new {@code ValidationError} instance using the specified field, message, and key.
     *
     * @param field   The name of the field that caused the validation error. Must not be null.
     * @param message A description of the validation error. Must not be null.
     * @param key     A key that identifies the type of validation error, useful for localization. Must not be null.
     * @return A new {@code ValidationError} instance.
     */
    public static ValidationError of(String field, String message, String key) {
        return new ValidationError(field, message, key);
    }

    /**
     * Returns the name of the field that caused the validation error.
     *
     * @return The name of the field. Never null.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the error message describing the validation failure.
     *
     * @return The error message. Never null.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the key that identifies the type of validation error.
     *
     * @return The error key, which is useful for localization. Never null.
     */
    public String getKey() {
        return key;
    }
}
