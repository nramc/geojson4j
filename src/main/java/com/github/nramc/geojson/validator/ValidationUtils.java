package com.github.nramc.geojson.validator;

public final class ValidationUtils {

    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates the given object and throws a GeoJsonValidationException if validation errors are found.
     *
     * @param validatable The object to validate, which must implement the Validatable interface.
     * @param <T>         The type of the validatable object, which extends Validatable.
     * @return The validated object if no errors are found.
     * @throws GeoJsonValidationException if there are validation errors.
     */
    public static <T extends Validatable> T validateAndThrowErrorIfInvalid(T validatable) {
        ValidationResult validationResult = validatable.validate();
        if (validationResult.hasErrors()) {
            throw new GeoJsonValidationException("GeoJson Invalid", validationResult.getErrors());
        }
        return validatable;
    }
}
