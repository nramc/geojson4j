package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.geojson.constant.GeoJsonType;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a GeoJSON Point geometry.
 * <p>
 * A GeoJSON Point is defined by a single {@link Position} which includes the coordinates
 * (longitude, latitude, and optional altitude) of the point. This class provides methods
 * to create and validate Point objects in compliance with the GeoJSON specification.
 * </p>
 *
 * <p>Note: The constructor does not perform validation eagerly. To validate a {@code Point}
 * object, the {@link #validate()}  or {@link #isValid()} method must be called explicitly.
 * Alternatively, you can use the static factory methods that validate the data eagerly
 * and throw {@code GeoJsonValidationException} if the data is invalid.
 * </p>
 *
 * <p>Example usage:
 * <pre>{@code
 * Point point = Point.of(40.7128, -74.0060);
 * }</pre></p>
 */
public final class Point extends Geometry {
    private final String type;
    private final Position coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public Point() {
        this(null, null);
    }

    /**
     * Constructs a new {@code Point} with the specified type and coordinates.
     * <p>If object created using constructor directly, then validation does not performed eagerly.
     * To perform validation, use below options,
     * <ol>
     *     <li>{@link Point#validate()} to perform validation and return result</li>
     *     <li>{@link Point#isValid()} to check whether GeoJson valid or not.</li>
     * </ol>
     * </p>
     *
     * @param type        The type of the GeoJSON object, which must be "Point".
     * @param coordinates The {@link Position} representing the coordinates of the point.
     */
    @JsonCreator
    public Point(String type, Position coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    /**
     * Creates a new {@code Point} instance with the specified coordinates, performing validation.
     *
     * @param coordinates The {@link Position} representing the coordinates of the point.
     * @return A validated {@code Point} object.
     * @throws GeoJsonValidationException if the provided coordinates are invalid.
     */
    public static Point of(Position coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Point(GeoJsonType.POINT, coordinates));
    }

    /**
     * Creates a new {@code Point} instance from longitude and latitude values, performing validation.
     *
     * @param longitude The longitude of the point.
     * @param latitude  The latitude of the point.
     * @return A validated {@code Point} object.
     * @throws GeoJsonValidationException if the provided longitude or latitude are invalid.
     */
    public static Point of(long longitude, long latitude) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Point(GeoJsonType.POINT, Position.of(longitude, latitude)));
    }

    /**
     * Creates a new {@code Point} instance from longitude, latitude, and altitude values, performing validation.
     *
     * @param longitude The longitude of the point.
     * @param latitude  The latitude of the point.
     * @param altitude  The altitude of the point.
     * @return A validated {@code Point} object.
     * @throws GeoJsonValidationException if the provided values are invalid.
     */
    public static Point of(long longitude, long latitude, long altitude) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Point(GeoJsonType.POINT, Position.of(longitude, latitude, altitude)));
    }

    /**
     * Validates the {@code Point} object, checking for any errors in the type or coordinates.
     *
     * @return A {@link ValidationResult} object containing any validation errors found.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, GeoJsonType.POINT)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, GeoJsonType.POINT), "type.invalid"));
        }
        if (coordinates == null) {
            errors.add(ValidationError.of("coordinates", "coordinates should not be empty/blank", "coordinates.invalid.empty"));
        } else {
            ValidationResult coordinateValidationResult = coordinates.validate();
            if (coordinateValidationResult.hasErrors()) {
                errors.addAll(coordinateValidationResult.getErrors());
            }
        }

        return new ValidationResult(errors);
    }

}
