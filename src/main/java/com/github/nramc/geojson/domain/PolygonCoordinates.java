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
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the coordinates of a Polygon in GeoJSON format.
 * <p>
 * A Polygon is defined by a series of linear rings, where the first ring is the exterior of the polygon,
 * and any subsequent rings represent holes (interior regions) within the polygon.
 * The exterior and holes are validated to ensure they adhere to the rules of GeoJSON polygons:
 * - The exterior must be a closed ring with at least 4 positions.
 * - Each hole must also be a closed ring, and can be empty if no holes exist.
 * </p>
 * <p>
 * This class is designed to be used with frameworks that serialize and deserialize GeoJSON objects, such as
 * Jackson for JSON parsing and ORM frameworks for persistence. The object can be validated using the {@link #validate()} method
 * to ensure its integrity according to GeoJSON standards.
 * </p>
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * List<Position> exterior = Arrays.asList(Position.of(0, 0), Position.of(0, 1), Position.of(1, 1), Position.of(1, 0), Position.of(0, 0));
 * List<Position> hole = Arrays.asList(Position.of(0.2, 0.2), Position.of(0.2, 0.8), Position.of(0.8, 0.8), Position.of(0.8, 0.2), Position.of(0.2, 0.2));
 * PolygonCoordinates polygon = PolygonCoordinates.of(exterior, List.of(hole));
 * }</pre>
 *
 * <p>
 * The `validate()` method can be called to check whether the polygon coordinates are valid:
 * </p>
 * <pre>{@code
 * ValidationResult validationResult = polygon.validate();
 * if (validationResult.hasErrors()) {
 *     // Handle validation errors
 * }
 * }</pre>
 */
public class PolygonCoordinates implements Validatable, Serializable {
    private final List<Position> exterior;
    private final List<List<Position>> holes;

    /**
     * Default constructor. Initializes the PolygonCoordinates with null values.
     * <p>
     * This constructor is required for certain frameworks (e.g., ORM frameworks) and serialization mechanisms
     * that need to instantiate objects without arguments. It is recommended to call {@link #validate()} or
     * {@link #isValid()} method after using this constructor to ensure the object is in a valid state.
     * </p>
     */
    public PolygonCoordinates() {
        this(null, null);
    }

    /**
     * Constructs a PolygonCoordinates instance from a list of linear rings.
     * <p>
     * The first linear ring is assigned to the exterior of the polygon,
     * and the remaining rings are assigned to the holes.
     * </p>
     *
     * @param linearRings List of linear rings, where the first list is the exterior and the others are the holes.
     */
    @JsonCreator
    public PolygonCoordinates(final List<List<Position>> linearRings) {
        this(CollectionUtils.isNotEmpty(linearRings) ? linearRings.getFirst() : List.of(),
                linearRings.size() <= 1 ? List.of() : linearRings.subList(1, linearRings.size()));
    }

    /**
     * Constructs a PolygonCoordinates instance with specified exterior and hole coordinates.
     *
     * @param exterior The exterior ring of the polygon.
     * @param holes    The hole rings inside the polygon.
     */
    public PolygonCoordinates(final List<Position> exterior, final List<List<Position>> holes) {
        this.exterior = CollectionUtils.isNotEmpty(exterior) ? Collections.unmodifiableList(exterior) : null;
        this.holes = CollectionUtils.isNotEmpty(holes) ? Collections.unmodifiableList(holes) : null;
    }

    /**
     * Creates and validates a new {@link PolygonCoordinates} instance from the specified varargs of linear rings.
     * <p>
     * This method constructs a {@link PolygonCoordinates} object using a varargs parameter, where each provided
     * {@link List} of {@link Position} objects represents a linear ring of the polygon. The first linear ring
     * is the exterior ring, and any additional rings are treated as interior holes. The created instance is
     * immediately validated to ensure it adheres to GeoJSON specifications. If the validation fails, an exception
     * is thrown.
     * </p>
     *
     * @param positions A varargs of {@link List} objects, each containing {@link Position} objects that form
     *                  a linear ring. The first {@link List} represents the exterior ring, while any additional
     *                  lists are interior rings (holes). Each linear ring must be closed and have at least 4 positions,
     *                  with the first and last positions being identical.
     * @return A validated {@link PolygonCoordinates} object constructed from the given linear rings.
     * @throws GeoJsonValidationException If the created {@link PolygonCoordinates} object is invalid according
     *                                    to GeoJSON specifications, such as having an insufficient number of positions
     *                                    or a non-closed linear ring.
     * @see PolygonCoordinates
     */
    @SafeVarargs
    public static PolygonCoordinates of(List<Position>... positions) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new PolygonCoordinates(Arrays.asList(positions)));
    }

    /**
     * Creates and validates a new {@link PolygonCoordinates} instance from the specified linear rings.
     * <p>
     * This method constructs a {@link PolygonCoordinates} object using the given list of linear rings,
     * which includes an exterior ring and optional interior rings (holes). The created instance is immediately
     * validated to ensure compliance with GeoJSON specifications. If the validation fails, an exception is thrown.
     * </p>
     *
     * @param linearRings A list of lists of {@link Position} objects, where the first list represents
     *                    the exterior linear ring of the polygon, and the subsequent lists represent
     *                    the interior rings (holes). Each linear ring must be closed and contain at least
     *                    4 positions, with the first and last positions being the same.
     * @return A validated {@link PolygonCoordinates} object constructed from the given linear rings.
     * @throws GeoJsonValidationException If the created {@link PolygonCoordinates} object is invalid according
     *                                    to GeoJSON specifications, such as an insufficient number of positions
     *                                    or a non-closed linear ring.
     */
    public static PolygonCoordinates of(List<List<Position>> linearRings) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new PolygonCoordinates(linearRings));
    }

    /**
     * Creates and validates a new {@link PolygonCoordinates} instance with the specified exterior and holes.
     * <p>
     * This method constructs a {@link PolygonCoordinates} object using the given exterior and holes,
     * and immediately validates the created instance. If the validation fails, an exception is thrown.
     * </p>
     *
     * @param exterior The list of {@link Position} objects representing the exterior linear ring of the polygon.
     *                 The exterior must be a closed linear ring with at least 4 positions.
     * @param holes    A list of lists of {@link Position} objects, where each inner list represents a hole
     *                 (interior linear ring) of the polygon. Each hole must also be a closed linear ring,
     *                 or this parameter can be an empty list if no holes exist.
     * @return A validated {@link PolygonCoordinates} object constructed with the given exterior and holes.
     * @throws GeoJsonValidationException If the created {@link PolygonCoordinates} object is invalid according
     *                                    to GeoJSON specifications, such as an insufficient number of positions
     *                                    or a non-closed ring.
     */
    public static PolygonCoordinates of(final List<Position> exterior, final List<List<Position>> holes) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new PolygonCoordinates(exterior, holes));
    }

    /**
     * Returns the coordinates of the polygon, consisting of the exterior and holes.
     *
     * @return A list containing the exterior and hole coordinates.
     */
    @JsonValue
    public List<List<Position>> getCoordinates() {
        List<List<Position>> coordinates = new ArrayList<>();
        coordinates.add(this.exterior);
        if (CollectionUtils.isNotEmpty(this.holes)) {
            coordinates.addAll(this.holes);
        }
        return coordinates;
    }

    /**
     * Returns the list of {@link Position} objects representing the exterior coordinates of the polygon.
     *
     * @return the exterior coordinates of the polygon
     */
    public List<Position> getExterior() {
        return exterior;
    }

    /**
     * Returns the list of holes, where each hole is represented by a list of {@link Position} objects.
     *
     * @return the list of holes, or an empty list if no holes are present
     */
    public List<List<Position>> getHoles() {
        return holes;
    }

    @SuppressWarnings("java:S1192") // String literals should not be duplicated
    private static Set<ValidationError> validateLinerRing(List<Position> linearRing) {
        Set<ValidationError> errors = new HashSet<>();
        if (CollectionUtils.isEmpty(linearRing)) {
            errors.add(ValidationError.of("coordinates", "Exterior linear ring should not be blank/empty.", "coordinates.exterior.ring.empty"));
        }
        if (CollectionUtils.size(linearRing) < 4) {
            errors.add(ValidationError.of("coordinates", "Ring '%s' must contain at least four positions.".formatted(linearRing), "coordinates.ring.length.invalid"));
        }
        if (CollectionUtils.isNotEmpty(linearRing) && !linearRing.getFirst().equals(linearRing.getLast())) {
            errors.add(ValidationError.of("coordinates", "Ring '%s', first and last position must be the same.".formatted(linearRing), "coordinates.ring.circle.invalid"));
        }
        CollectionUtils.emptyIfNull(linearRing).stream().map(Position::validate).filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors).forEach(errors::addAll);
        return errors;
    }


    /**
     * Validates the polygon coordinates (exterior and holes).
     * <p>
     * This method checks that the exterior and holes are valid according to the rules for polygons:
     * - The exterior must be a valid linear ring (at least 4 positions, first equals last).
     * - Each hole must also be a valid linear ring.
     * </p>
     *
     * @return A {@link ValidationResult} containing any validation errors.
     */
    @Override
    public ValidationResult validate() {

        Set<ValidationError> errors = new HashSet<>(validateLinerRing(exterior));

        CollectionUtils.emptyIfNull(holes).stream().map(PolygonCoordinates::validateLinerRing)
                .filter(CollectionUtils::isNotEmpty).forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Returns a string representation of the {@link PolygonCoordinates} object.
     * The string includes the {@code exterior} coordinates and any {@code holes}.
     *
     * @return a string representation of the {@link PolygonCoordinates} object,
     * formatted as "PolygonCoordinates{exterior=[...], holes=[...]}".
     */
    @Override
    public String toString() {
        List<List<Position>> coordinates = new ArrayList<>();
        coordinates.add(CollectionUtils.isNotEmpty(exterior) ? exterior : Collections.emptyList());
        coordinates.addAll(CollectionUtils.isNotEmpty(holes) ? holes : Collections.emptyList());
        return coordinates.toString();
    }

    /**
     * Compares this {@link PolygonCoordinates} object to another object for equality.
     * Two {@code PolygonCoordinates} objects are considered equal if their {@code exterior}
     * coordinates and {@code holes} are equal.
     *
     * @param o the object to compare this {@link PolygonCoordinates} to
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolygonCoordinates that)) return false;

        return exterior.equals(that.exterior) && Objects.equals(holes, that.holes);
    }

    /**
     * Computes a hash code for this {@link PolygonCoordinates} object based on its {@code exterior}
     * coordinates and {@code holes}. The hash code is computed using the {@link #exterior} hash code
     * and the hash code of {@link #holes}.
     *
     * @return the hash code for this {@link PolygonCoordinates} object
     */
    @Override
    public int hashCode() {
        int result = exterior.hashCode();
        result = 31 * result + Objects.hashCode(holes);
        return result;
    }
}
