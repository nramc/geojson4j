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
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;

/**
 * A class representing a GeoJSON LineString geometry object.
 * A LineString is a collection of two or more {@link Position} coordinates
 * that represent a line in a two-dimensional or three-dimensional space.
 */
public final class LineString extends Geometry {
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
    public LineString() {
        this(null, null);
    }

    /**
     * Constructs a new {@link LineString} object with the specified type and coordinates.
     * This constructor is typically used for deserialization of JSON data.
     *
     * @param type        The type of GeoJSON geometry, expected to be "LineString".
     * @param coordinates The list of coordinates that make up the LineString, which must be a valid list of {@link Position}.
     */
    @JsonCreator
    public LineString(String type, List<Position> coordinates) {
        this.type = type;
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
        return ValidationUtils.validateAndThrowErrorIfInvalid(new LineString(LINE_STRING, Arrays.stream(coordinates).toList()));
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
}
