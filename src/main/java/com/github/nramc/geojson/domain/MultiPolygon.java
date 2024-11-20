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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POLYGON;

/**
 * Represents a GeoJSON MultiPolygon object, which is a collection of multiple {@link PolygonCoordinates} objects.
 * This class extends the {@link Geometry} base class and provides methods to create, manage, and validate
 * MultiPolygon instances according to the GeoJSON specification.
 * <p>
 * A MultiPolygon is used to represent multiple polygon geometries in a single GeoJSON object. Each
 * {@link PolygonCoordinates} object in the collection represents an individual polygon.
 * </p>
 *
 * <p>
 * The class includes various factory methods to create instances of MultiPolygon and validation logic to
 * ensure the MultiPolygon conforms to the GeoJSON specification.
 * </p>
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * PolygonCoordinates PolygonCoordinate1 = PolygonCoordinates.of(Arrays.asList(Position.of(0, 0), Position.of(0, 1), Position.of(1, 1), Position.of(1, 0), Position.of(0, 0)));
 * PolygonCoordinates PolygonCoordinate2 = PolygonCoordinates.of(Arrays.asList(Position.of(0.2, 0.2), Position.of(0.2, 0.8), Position.of(0.8, 0.8), Position.of(0.8, 0.2), Position.of(0.2, 0.2)));
 * MultiPolygon multiPolygon = MultiPolygon.of(PolygonCoordinate1, PolygonCoordinate2);
 * }</pre>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.7">RFC 7946 - Section 3.1.7</a></p>
 *
 * @see Geometry
 * @see PolygonCoordinates
 */
public final class MultiPolygon extends Geometry {
    private final String type;
    private final List<PolygonCoordinates> coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks) and
     * serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor, it is
     * recommended to call the {@link #validate()} method to ensure the object is in a valid state.
     * </p>
     */
    public MultiPolygon() {
        this(null, null);
    }

    /**
     * Constructs a {@link MultiPolygon} with the specified type and list of {@link PolygonCoordinates}.
     * This constructor does not perform any validation. This constructor is typically used for deserialization of JSON data.
     * After using this constructor, it is recommended to call the {@link #validate()} method to ensure the object is in a valid state.
     *
     * @param type        The GeoJSON type, which should be "MultiPolygon" for valid MultiPolygon objects.
     * @param coordinates The list of {@link PolygonCoordinates} objects representing the individual polygons.
     */
    @JsonCreator
    public MultiPolygon(@JsonProperty("type") String type, @JsonProperty("coordinates") List<PolygonCoordinates> coordinates) {
        this.type = type;
        this.coordinates = CollectionUtils.isNotEmpty(coordinates) ? Collections.unmodifiableList(coordinates) : null;
    }

    /**
     * Factory method to create a {@link MultiPolygon} from a variable number of {@link PolygonCoordinates} objects.
     * <p>
     * This method constructs a new {@link MultiPolygon} using the provided array of {@link PolygonCoordinates} and
     * immediately validates the constructed instance.
     * If the created MultiPolygon is invalid, a {@link GeoJsonValidationException} will be thrown with detailed
     * validation errors.
     * </p>
     *
     * @param coordinates A variable number of {@link PolygonCoordinates} objects representing the individual polygons
     *                    that make up the MultiPolygon. Must not be empty or null.
     * @return A validated {@link MultiPolygon} object.
     * @throws GeoJsonValidationException if the constructed MultiPolygon is invalid according to the GeoJSON specification.
     */
    public static MultiPolygon of(PolygonCoordinates... coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiPolygon(MULTI_POLYGON, List.of(coordinates)));
    }

    /**
     * Factory method to create a {@link MultiPolygon} from a list of {@link PolygonCoordinates} objects.
     * <p>
     * This method constructs a new {@link MultiPolygon} with the specified coordinates and validates the created instance.
     * If the constructed MultiPolygon does not pass validation, a {@link GeoJsonValidationException} will
     * be thrown with details about the validation errors.
     * </p>
     *
     * @param coordinates The list of {@link PolygonCoordinates} objects representing the individual polygons
     *                    that make up the MultiPolygon. Must not be empty or null.
     * @return A validated {@link MultiPolygon} object.
     * @throws GeoJsonValidationException if the constructed MultiPolygon is invalid according to the GeoJSON specification.
     */
    public static MultiPolygon of(List<PolygonCoordinates> coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiPolygon(MULTI_POLYGON, coordinates));
    }

    /**
     * Validates the {@link MultiPolygon} object to ensure it adheres to the GeoJSON specification.
     * <p>
     * The validation checks include:
     * - Ensuring the "type" field is "MultiPolygon".
     * - Ensuring the coordinates list is not empty and contains at least one polygon.
     * - Delegating validation to each {@link PolygonCoordinates} object in the list.
     * </p>
     *
     * @return A {@link ValidationResult} object containing any validation errors found.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, MULTI_POLYGON)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, MULTI_POLYGON), "type.invalid"));
        }

        if (CollectionUtils.size(coordinates) < 1) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, at least one position required", "coordinates.invalid.min.length"));
        }

        CollectionUtils.emptyIfNull(coordinates).stream()
                .map(PolygonCoordinates::validate)
                .filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Gets the type of the GeoJson.
     *
     * @return The type of the GeoJson, which is "MultiPolygon".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Retrieves the list of polygon coordinates that define the MultiPolygon geometry.
     *
     * @return A list of {@link PolygonCoordinates} objects representing the MultiPolygon's coordinates.
     */
    public List<PolygonCoordinates> getCoordinates() {
        return coordinates;
    }

    /**
     * Returns a string representation of the MultiPolygon object.
     * The string is formatted as "MultiPolygon{type='typeValue', coordinates=coordinatesValue}"
     * where "typeValue" is the type of the GeoJSON object, and "coordinatesValue"
     * represents the MultiPolygon's coordinates.
     *
     * @return A formatted string representing the MultiPolygon object.
     */
    @Override
    public String toString() {
        return MessageFormat.format("MultiPolygon'{'type=''{0}'', coordinates={1}'}'", type, coordinates);
    }

    /**
     * Compares this MultiPolygon object with another object for equality.
     * <p>
     * The comparison checks if both objects are of the same type and if their
     * type and coordinates fields are equal.
     *
     * @param o The object to compare with this MultiPolygon.
     * @return {@code true} if the specified object is equal to this MultiPolygon, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiPolygon that)) {
            return false;
        }

        return type.equals(that.type) && coordinates.equals(that.coordinates);
    }

    /**
     * Computes the hash code for this MultiPolygon.
     * <p>
     * The hash code is calculated based on the type and coordinates fields,
     * ensuring consistent hashing for use in hash-based collections.
     *
     * @return The hash code value for this MultiPolygon.
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + coordinates.hashCode();
        return result;
    }
}
