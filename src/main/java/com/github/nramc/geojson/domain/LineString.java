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
import org.springframework.data.annotation.TypeAlias;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;

/**
 * Represents a GeoJSON LineString geometry, which is defined by an ordered list of two or more
 * {@link Position} objects. A LineString describes a series of connected points, forming a
 * continuous line.
 *
 * <p>The LineString class ensures the validity of the coordinates by enforcing that the list
 * contains at least two positions. It provides methods to access the coordinates and validate
 * the integrity of the LineString.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 *   List<Position> positions = List.of(Position.of(100.0, 0.0), Position.of(101.0, 1.0));
 *   LineString lineString = new LineString(positions);
 * }</pre></p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.4">RFC 7946 - Section 3.1.4</a></p>
 *
 * @see Position
 * @see Geometry
 */
@TypeAlias(LINE_STRING)
public final class LineString extends Geometry {
    private final List<Position> coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public LineString() {
        this(LINE_STRING, null);
    }

    /**
     * Constructs a new {@link LineString} object with the specified type and coordinates.
     * This constructor is typically used for deserialization of JSON data.
     *
     * @param type        The type of GeoJSON geometry, expected to be "LineString".
     * @param coordinates The list of coordinates that make up the LineString, which must be a valid list of {@link Position}.
     */
    @JsonCreator
    public LineString(@JsonProperty("type") String type, @JsonProperty("coordinates") List<Position> coordinates) {
        super(type);
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    /**
     * Creates a new {@link LineString} with the specified coordinates.
     * The coordinates are validated before the LineString object is created.
     *
     * @param coordinates A list of {@link Position} objects representing the coordinates of the LineString.
     * @return A new {@link LineString} object if the coordinates are valid.
     * @throws com.github.nramc.geojson.validator.GeoJsonValidationException If validation of the coordinates fails.
     */
    public static LineString of(List<Position> coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new LineString(LINE_STRING, coordinates));
    }

    /**
     * Creates a new {@link LineString} with the specified coordinates.
     * The coordinates are validated before the LineString object is created.
     *
     * @param coordinates A varargs array of {@link Position} objects representing the coordinates of the LineString.
     * @return A new {@link LineString} object if the coordinates are valid.
     * @throws com.github.nramc.geojson.validator.GeoJsonValidationException If validation of the coordinates fails.
     */
    public static LineString of(Position... coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new LineString(LINE_STRING,
                ArrayUtils.isNotEmpty(coordinates) ? Arrays.asList(coordinates) : List.of()));
    }

    /**
     * Validates the LineString object by checking its type and coordinates.
     * If any validation errors are found, they are added to a set of errors.
     *
     * @return A {@link ValidationResult} containing any validation errors.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, LINE_STRING)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, LINE_STRING), "type.invalid"));
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
     * Gets the type of the GeoJson.
     *
     * @return The type of the GeoJson, which is "LineString".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Retrieves the list of coordinates that define this LineString.
     *
     * @return an unmodifiable list of {@link Position} objects representing the coordinates
     * of the LineString. The list contains at least two positions.
     */
    public List<Position> getCoordinates() {
        return coordinates;
    }

    /**
     * Returns a string representation of the LineString.
     *
     * @return a formatted string containing the type and coordinates of the LineString.
     */
    @Override
    public String toString() {
        return MessageFormat.format("LineString'{'type=''{0}'', coordinates={1}'}'", type, coordinates);
    }

    /**
     * Compares this {@code LineString} with the specified object for equality.
     *
     * @param o the object to compare with this {@code LineString}
     * @return {@code true} if the specified object is equal to this {@code LineString}, otherwise {@code false}
     * <p>
     * The method checks:
     * <ul>
     *     <li>If the current instance is compared with itself, it returns {@code true}.</li>
     *     <li>If the object is not an instance of {@code LineString}, it returns {@code false}.</li>
     *     <li>If both objects have the same type and coordinates, it returns {@code true}; otherwise, it returns {@code false}.</li>
     * </ul>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineString that)) {
            return false;
        }

        return type.equals(that.type) && coordinates.equals(that.coordinates);
    }

    /**
     * Returns the hash code value for this {@code LineString}.
     *
     * @return the hash code value, computed based on the {@code type} and {@code coordinates} fields
     * <p>
     * The method generates a hash code using the following logic:
     * - Initializes the result with the hash code of the {@code type}.
     * - Updates the result by multiplying it by 31 and adding the hash code of the {@code coordinates}.
     * This approach ensures a well-distributed hash code for the object.
     * </p>
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + coordinates.hashCode();
        return result;
    }
}
