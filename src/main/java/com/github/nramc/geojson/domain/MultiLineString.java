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
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;

/**
 * A class representing a GeoJSON MultiLineString geometry object.
 * A MultiLineString is a collection of {@link LineString} objects, each of which consists of two or more {@link Position} coordinates.
 * The MultiLineString object allows for the representation of multiple lines.
 *
 * <p>Example usage:
 * <pre>{@code
 *  List<Position> line1 = List.of(Position.of(30, 10), Position.of(10, 30), Position.of(40, 40));
 *  List<Position> line2 = List.of(Position.of(15, 5), Position.of(40, 10), Position.of(10, 20), Position.of(5, 10));
 *  MultiLineString multiLineString = MultiLineString.of(List.of(line1, line2));
 * }</pre></p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.5">RFC 7946 - Section 3.1.5</a></p>
 *
 * @see Position
 * @see Geometry
 */
public final class MultiLineString extends Geometry {
    private final String type;
    private final List<List<Position>> coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public MultiLineString() {
        this(null, null);
    }

    /**
     * Constructs a new {@link MultiLineString} object with the specified type and coordinates.
     * This constructor is typically used for deserialization of JSON data.
     *
     * @param type        The type of GeoJSON geometry, expected to be "MultiLineString".
     * @param coordinates The list of coordinates, where each element is a list of {@link Position} objects representing a LineString.
     */
    @JsonCreator
    public MultiLineString(@JsonProperty("type") String type, @JsonProperty("coordinates") List<List<Position>> coordinates) {
        this.type = type;
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    /**
     * Creates a new {@link MultiLineString} with the specified coordinates.
     * The coordinates are validated before the MultiLineString object is created.
     *
     * @param coordinates A list of lists of {@link Position} objects representing multiple LineString coordinates.
     * @return A new {@link MultiLineString} object if the coordinates are valid.
     * @throws GeoJsonValidationException If validation of the coordinates fails.
     */
    public static MultiLineString of(List<List<Position>> coordinates) throws GeoJsonValidationException {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiLineString(MULTI_LINE_STRING, coordinates));
    }

    /**
     * Creates a new {@link MultiLineString} with the specified coordinates.
     * This method accepts a varargs array of {@link Position} lists representing multiple LineStrings.
     * The coordinates are validated before the MultiLineString object is created.
     *
     * @param coordinates A varargs array of {@link Position} lists representing multiple LineString coordinates.
     * @return A new {@link MultiLineString} object if the coordinates are valid.
     * @throws GeoJsonValidationException If validation of the coordinates fails.
     */
    @SafeVarargs
    public static MultiLineString of(List<Position>... coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiLineString(MULTI_LINE_STRING, Arrays.stream(coordinates).toList()));
    }

    /**
     * Validates the MultiLineString object by checking its type and coordinates.
     * If any validation errors are found, they are added to a set of errors.
     *
     * @return A {@link ValidationResult} containing any validation errors.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        // Validate type
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, MULTI_LINE_STRING)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, MULTI_LINE_STRING), "type.invalid"));
        }

        // Validate coordinates
        if (CollectionUtils.isEmpty(coordinates)) {
            errors.add(ValidationError.of("coordinates", "coordinates should not be empty/blank", "coordinates.invalid.empty"));
        }

        // Validate each line in the coordinates
        if (coordinates.stream().anyMatch(positions -> positions.size() < 2)) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, minimum 2 positions required", "coordinates.invalid.min.length"));
        }

        // Validate each position in the coordinates
        coordinates.stream().flatMap(Collection::stream)
                .map(Position::validate).filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Gets the type of the GeoJson.
     *
     * @return The type of the GeoJson, which is "MultiLineString".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Retrieves the list of coordinate sequences for this geometry.
     * Each inner list represents a sequence of {@link Position} objects, forming a line.
     *
     * @return A list of coordinate sequences, where each sequence is a list of {@link Position} objects.
     */
    public List<List<Position>> getCoordinates() {
        return coordinates;
    }

    /**
     * Returns a string representation of this {@link MultiLineString} object,
     * including its type and the list of coordinates.
     *
     * @return A string representing the {@link MultiLineString}
     */
    @Override
    public String toString() {
        return MessageFormat.format("MultiLineString'{'type=''{0}'', coordinates={1}'}'", type, coordinates);
    }

    /**
     * Compares this {@link MultiLineString} object with another object for equality.
     * Two {@link MultiLineString} objects are considered equal if their type and coordinates are the same.
     *
     * @param o The object to compare with.
     * @return {@code true} if this {@link MultiLineString} is equal to the specified object,
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiLineString that)) {
            return false;
        }

        return type.equals(that.type) && coordinates.equals(that.coordinates);
    }

    /**
     * Computes the hash code for this {@link MultiLineString} object.
     * The hash code is computed based on the {@link #type} and {@link #coordinates} fields.
     * This method ensures that two {@link MultiLineString} objects with the same type and coordinates
     * will produce the same hash code.
     *
     * @return the hash code value for this {@link MultiLineString}.
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + coordinates.hashCode();
        return result;
    }
}
