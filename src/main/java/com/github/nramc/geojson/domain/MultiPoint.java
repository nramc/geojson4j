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
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POINT;

/**
 * Represents a GeoJSON MultiPoint geometry object.
 * <p>
 * A MultiPoint object contains multiple Position objects, which represent individual points in 2D or 3D space.
 * The MultiPoint class validates the coordinates and ensures they meet the expected criteria for GeoJSON format.
 * </p>
 * <p>Example usage:
 * <pre>{@code
 * Point point = Point.of(40.7128, -74.0060);
 * }
 * </pre>
 * </p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.3">RFC 7946 - Section 3.1.3</a></p>
 * @see Position
 * @see Geometry
 */
public final class MultiPoint extends Geometry {
    private final String type;
    private final List<Position> coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public MultiPoint() {
        this(null, null);
    }

    /**
     * Creates a MultiPoint object with the specified type and coordinates.
     * This constructor is typically used for deserialization of JSON data.
     * <p>
     * The coordinates list is wrapped in an unmodifiable list to ensure immutability.
     * </p>
     *
     * @param type        The type of the geometry, typically "MultiPoint".
     * @param coordinates The list of positions that make up the MultiPoint.
     */
    @JsonCreator
    public MultiPoint(@JsonProperty("type") String type, @JsonProperty("coordinates") List<Position> coordinates) {
        this.type = type;
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    /**
     * Factory method to create a MultiPoint instance from a list of coordinates.
     *
     * @param coordinates A list of Position objects representing the coordinates.
     * @return A new MultiPoint instance with the given coordinates.
     * @throws com.github.nramc.geojson.validator.GeoJsonValidationException if the provided coordinates are invalid.
     */
    public static MultiPoint of(List<Position> coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiPoint(MULTI_POINT, coordinates));
    }

    /**
     * Factory method to create a MultiPoint instance from a varargs array of Position objects.
     *
     * @param positions A varargs array of Position objects representing the coordinates.
     * @return A new MultiPoint instance with the given coordinates.
     * @throws com.github.nramc.geojson.validator.GeoJsonValidationException if the provided coordinates are invalid.
     */
    public static MultiPoint of(Position... positions) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(
                new MultiPoint(MULTI_POINT, ArrayUtils.isNotEmpty(positions) ? Arrays.asList(positions) : List.of()));
    }

    /**
     * Gets the type of the geometry.
     *
     * @return The type of the geometry, which is "MultiPoint" for this class.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the list of coordinates (Position objects) that make up the MultiPoint.
     *
     * @return An unmodifiable list of Position objects.
     */
    public List<Position> getCoordinates() {
        return coordinates;
    }

    /**
     * Validates the MultiPoint geometry.
     * <p>
     * The validation checks the following:
     * <ul>
     *     <li>Validates that the type is "MultiPoint".</li>
     *     <li>Ensures the coordinates are not empty or null.</li>
     *     <li>Checks that there are at least 2 coordinates.</li>
     *     <li>Validates each Position in the coordinates list.</li>
     * </ul>
     * </p>
     *
     * @return A ValidationResult object containing any validation errors.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, MULTI_POINT)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, MULTI_POINT), "type.invalid"));
        }
        if (CollectionUtils.isEmpty(coordinates)) {
            errors.add(ValidationError.of("coordinates", "coordinates should not be empty/blank", "coordinates.invalid.empty"));
        }
        if (CollectionUtils.size(coordinates) < 2) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, minimum 2 positions required", "coordinates.invalid.min.length"));
        }

        CollectionUtils.emptyIfNull(coordinates).stream().map(Position::validate)
                .filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Returns a string representation of the {@link MultiPoint} object in the format:
     * <pre>
     * MultiPoint{type='MultiPoint', coordinates=[...]}
     * </pre>
     *
     * @return A formatted string with the type and coordinates of the MultiPoint.
     */
    @Override
    public String toString() {
        return MessageFormat.format("MultiPoint'{'type=''{0}'', coordinates={1}'}'", type, coordinates);
    }

    /**
     * Checks if this MultiPoint is equal to another object.
     *
     * @param o The object to compare with this MultiPoint.
     * @return true if the object is a MultiPoint with the same type and coordinates, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiPoint that)) return false;

        return Objects.equals(type, that.type) && Objects.equals(coordinates, that.coordinates);
    }

    /**
     * Computes the hash code for this MultiPoint.
     *
     * @return The hash code, based on the type and coordinates.
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(coordinates);
        return result;
    }
}
