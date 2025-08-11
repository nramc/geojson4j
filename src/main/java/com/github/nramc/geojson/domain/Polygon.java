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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a GeoJSON Polygon object, which is defined by a series of linear rings, including one exterior
 * ring and optionally several interior rings (holes). The {@link Polygon} class extends the {@link Geometry} base
 * class and provides methods for creating and validating Polygon objects.
 *
 * <p>A Polygon is structured according to the GeoJSON specification, where:
 * - The exterior ring defines the boundary of the polygon.
 * - Interior rings (holes) define regions within the polygon that are excluded from the area.
 * The {@link PolygonCoordinates} object is used to store and manage the rings.
 * </p>
 *
 * <p>The Polygon object supports various factory methods to construct instances from exterior and interior rings.</p>
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * List<Position> exterior = Arrays.asList(Position.of(0, 0), Position.of(0, 1), Position.of(1, 1), Position.of(1, 0), Position.of(0, 0));
 * List<Position> hole = Arrays.asList(Position.of(0.2, 0.2), Position.of(0.2, 0.8), Position.of(0.8, 0.8), Position.of(0.8, 0.2), Position.of(0.2, 0.2));
 * Polygon polygon = Polygon.of(exterior, List.of(hole));
 * }</pre>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.6">RFC 7946 - Section 3.1.6</a></p>
 *
 * @see Geometry
 * @see PolygonCoordinates
 * @see Position
 */
public final class Polygon extends Geometry {
    private final PolygonCoordinates coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks) and
     * serialization mechanisms that need to instantiate objects without arguments.
     */
    public Polygon() {
        this(POLYGON, null);
    }

    /**
     * Constructs a {@link Polygon} with the specified type and coordinates.
     *
     * <p>This constructor does not perform any validation. This constructor is typically used for deserialization of JSON data.
     * After using this constructor, it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     *
     * @param type        The GeoJSON type, which should be "Polygon" for valid Polygon objects.
     * @param coordinates The {@link PolygonCoordinates} object representing the exterior and interior rings.
     */
    @JsonCreator
    public Polygon(@JsonProperty("type") final String type, @JsonProperty("coordinates") final PolygonCoordinates coordinates) {
        super(type);
        this.coordinates = coordinates;
    }

    /**
     * Factory method to create a {@link Polygon} object using an exterior ring and a list of interior rings (holes).
     *
     * @param exterior The exterior ring of the polygon, represented as a list of {@link Position} objects.
     * @param holes    The interior rings (holes) of the polygon, represented as a list of lists of {@link Position} objects.
     * @return A validated {@link Polygon} object.
     */
    public static Polygon of(final List<Position> exterior, final List<List<Position>> holes) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Polygon(POLYGON, PolygonCoordinates.of(exterior, holes)));
    }

    /**
     * Factory method to create a {@link Polygon} object using an exterior ring and a variable number of interior rings (holes).
     *
     * @param exterior The exterior ring of the polygon, represented as a list of {@link Position} objects.
     * @param holes    A variable number of interior rings (holes), each represented as a list of {@link Position} objects.
     * @return A validated {@link Polygon} object.
     */
    @SafeVarargs
    public static Polygon of(final List<Position> exterior, final List<Position>... holes) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Polygon(POLYGON, PolygonCoordinates.of(exterior, List.of(holes))));
    }

    /**
     * Factory method to create a {@link Polygon} object from a {@link PolygonCoordinates} instance.
     *
     * @param coordinates The {@link PolygonCoordinates} instance representing the polygon's exterior and interior rings.
     * @return A validated {@link Polygon} object.
     */
    public static Polygon of(PolygonCoordinates coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Polygon(POLYGON, coordinates));
    }

    /**
     * Factory method to create a {@link Polygon} object from a list of linear rings.
     *
     * @param coordinates The list of linear rings, where the first ring is the exterior and subsequent rings are interior holes.
     * @return A validated {@link Polygon} object.
     */
    public static Polygon of(List<List<Position>> coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Polygon(POLYGON, PolygonCoordinates.of(coordinates)));
    }

    /**
     * Validates the {@link Polygon} object to ensure it adheres to the GeoJSON specification.
     *
     * <p>The validation checks include:
     * - Ensuring the "type" field is "Polygon".
     * - Ensuring the coordinates are not empty and contain at least one position.
     * - Delegating further validation to the {@link PolygonCoordinates} instance.
     * </p>
     *
     * @return A {@link ValidationResult} object containing any validation errors found.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !Objects.equals(type, POLYGON)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, POLYGON), "type.invalid"));
        }
        if (coordinates == null) {
            errors.add(ValidationError.of("coordinates", "coordinates should not be empty/blank", "coordinates.invalid.empty"));
        }
        if (coordinates != null && CollectionUtils.isEmpty(coordinates.getExterior())) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, at least one position required", "coordinates.invalid.min.length"));
        }
        if (coordinates != null) {
            ValidationResult coordinatesValidationResult = coordinates.validate();
            if (coordinatesValidationResult.hasErrors()) {
                errors.addAll(coordinatesValidationResult.getErrors());
            }
        }

        return new ValidationResult(errors);
    }

    /**
     * Gets the type of the GeoJson.
     *
     * @return The type of the GeoJson, which is "Polygon".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Retrieves the coordinates of the polygon, which consist of an exterior boundary
     * and optional interior holes.
     *
     * @return the {@link PolygonCoordinates} object representing the exterior and interior coordinates of the polygon.
     */
    public PolygonCoordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns a string representation of the Polygon object, including its type
     * and coordinates formatted in a human-readable way.
     *
     * @return a formatted string representing the Polygon, including type and coordinates.
     */
    @Override
    public String toString() {
        return MessageFormat.format("Polygon'{'type=''{0}'', coordinates={1}'}'", type, coordinates);
    }

    /**
     * Compares this Polygon object to another object for equality.
     * Two Polygons are considered equal if they have the same type and coordinates.
     *
     * @param o the object to compare with this Polygon
     * @return true if the specified object is equal to this Polygon, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Polygon polygon)) {
            return false;
        }

        return type.equals(polygon.type) && coordinates.equals(polygon.coordinates);
    }

    /**
     * Returns the hash code value for this Polygon object.
     * The hash code is computed using the hash codes of the type and coordinates fields.
     *
     * @return the hash code value of this Polygon
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        return 31 * result + coordinates.hashCode();
    }
}
