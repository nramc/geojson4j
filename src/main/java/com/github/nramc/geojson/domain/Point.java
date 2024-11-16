/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nramc.geojson.constant.GeoJsonType;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
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
     * This constructor is typically used for deserialization of JSON data.
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
    public Point(@JsonProperty("type") String type, @JsonProperty("coordinates") Position coordinates) {
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
    public static Point of(double longitude, double latitude) {
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
    public static Point of(double longitude, double latitude, double altitude) {
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

    /**
     * Gets the type of the GeoJson.
     *
     * @return The type of the GeoJson, which is "Point".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Gets the coordinates of the Point.
     *
     * @return The {@link Position} object representing the coordinates of the Point.
     */
    public Position getCoordinates() {
        return coordinates;
    }

    /**
     * Returns a string representation of the {@link Point} object in the format:
     * <pre>
     * Point{type='Point', coordinates=[longitude, latitude, altitude(optional)]}
     * </pre>
     *
     * @return A formatted string with the type and coordinates of the point.
     */
    @Override
    public String toString() {
        return MessageFormat.format("Point'{'type=''{0}'', coordinates={1}'}'", type, coordinates);
    }

    /**
     * Checks if this Point is equal to another object.
     *
     * @param o The object to compare with.
     * @return {@code true} if both objects are of type Point and have equal type and coordinates, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;

        return Objects.equals(type, point.type) && Objects.equals(coordinates, point.coordinates);
    }

    /**
     * Computes the hash code for this Point.
     *
     * @return The hash code, calculated using the type and coordinates.
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(coordinates);
        return result;
    }
}
