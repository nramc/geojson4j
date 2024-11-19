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
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.GEOMETRY_COLLECTION;

/**
 * Represents a GeoJSON GeometryCollection, which is a collection of multiple {@link Geometry} objects.
 * <p>
 * A GeometryCollection is used in GeoJSON to group different geometry types, such as points, lines, and polygons.
 * Each geometry in the collection must be a valid GeoJSON geometry, and nested GeometryCollections are not allowed.
 * </p>
 * <p>
 * This class supports validation through the {@link Validatable} interface to ensure GeoJSON compliance.
 * </p>
 * <p>
 * Note: The class is immutable, and all collections are unmodifiable to ensure thread-safety and immutability.
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * Point point = Point.of(40.7128, -74.0060);
 * MultiPoint multiPoint = MultiPoint.of(Position.of(100, 50),Position.of(110, 60),Position.of(150, 90));
 *
 * GeometryCollection geometryCollection = GeometryCollection.of(point, multiPoint);
 * }</pre>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.8">RFC 7946 - Section 3.1.8</a></p>
 * </p>
 */
public final class GeometryCollection extends Geometry {
    private final String type;
    private final List<Geometry> geometries;

    /**
     * Default no-argument constructor required for frameworks (e.g., ORM frameworks or serialization mechanisms).
     * <p>
     * This constructor does not perform any validation. It is recommended to call
     * {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public GeometryCollection() {
        this(null, null);
    }

    /**
     * Constructs a GeometryCollection with the specified type and list of geometries.
     * <p>
     * This constructor is used to create a GeometryCollection with the specified GeoJSON type and geometries. The
     * provided geometries are copied to ensure immutability.
     * </p>
     *
     * @param type       The type of the geometry collection, which must be "GeometryCollection".
     * @param geometries A list of {@link Geometry} objects to include in the collection. Must not contain null values
     *                   or nested GeometryCollections.
     */
    @JsonCreator
    public GeometryCollection(@JsonProperty("type") String type, @JsonProperty("geometries") List<Geometry> geometries) {
        this.type = type;
        this.geometries = CollectionUtils.isNotEmpty(geometries) ? List.copyOf(geometries) : List.of();
    }

    /**
     * Factory method to create a {@link GeometryCollection} from a list of geometries.
     * <p>
     * The method validates the created GeometryCollection using {@link ValidationUtils#validateAndThrowErrorIfInvalid(Validatable)}.
     * If the validation fails, a {@link GeoJsonValidationException} is thrown with details about the validation errors.
     * </p>
     *
     * @param geometries A list of {@link Geometry} objects to include in the GeometryCollection. Must not contain
     *                   nested GeometryCollections or be empty.
     * @return A validated {@link GeometryCollection} object.
     * @throws GeoJsonValidationException if the constructed GeometryCollection is invalid.
     */
    public static GeometryCollection of(List<Geometry> geometries) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new GeometryCollection(GEOMETRY_COLLECTION, geometries));
    }

    /**
     * Factory method to create a {@link GeometryCollection} from the given array of {@link Geometry} objects.
     * <p>
     * If the provided array is not empty, the method creates a list from the geometries; otherwise,
     * it uses an empty list. The method also validates the created {@code GeometryCollection}
     * and throws an exception if validation fails.
     *
     * @param geometries an array of {@link Geometry} objects to include in the collection.
     *                   If the array is empty or {@code null}, an empty collection is created.
     * @return a validated {@code GeometryCollection} containing the provided geometries.
     * @throws GeoJsonValidationException if the {@code GeometryCollection} is found to be invalid.
     */
    public static GeometryCollection of(Geometry... geometries) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new GeometryCollection(GEOMETRY_COLLECTION,
                ArrayUtils.isNotEmpty(geometries) ? Arrays.asList(geometries) : List.of()));
    }

    /**
     * Gets the type of the GeometryCollection.
     *
     * @return The type of the geometry, which is "GeometryCollection".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Gets the list of geometries contained in the GeometryCollection.
     *
     * @return An unmodifiable list of {@link Geometry} objects.
     */
    public List<Geometry> getGeometries() {
        return geometries;
    }

    /**
     * Validates the GeometryCollection to ensure it conforms to the GeoJSON specification.
     * <p>
     * Validation checks include:
     * <ul>
     *     <li>Type must be "GeometryCollection".</li>
     *     <li>The list of geometries must not contain nested GeometryCollections.</li>
     *     <li>All geometries must be valid GeoJSON geometries.</li>
     * </ul>
     * </p>
     *
     * @return A {@link ValidationResult} object containing validation errors, if any.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, GEOMETRY_COLLECTION)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, GEOMETRY_COLLECTION), "type.invalid"));
        }
        if (CollectionUtils.emptyIfNull(geometries).stream().anyMatch(geometry -> Objects.equals(geometry.getType(), GEOMETRY_COLLECTION))) {
            errors.add(ValidationError.of("geometries", "Field 'geometries' must not have nested 'GeometryCollection'", "geometries.invalid.nested.geometry"));
        }

        CollectionUtils.emptyIfNull(geometries).stream()
                .map(Validatable::validate)
                .filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Returns a string representation of the {@code GeometryCollection} object.
     * <p>
     * The format includes the type of the geometry and the list of geometries it contains.
     *
     * @return a formatted string representing the {@code GeometryCollection}.
     */
    @Override
    public String toString() {
        return MessageFormat.format("GeometryCollection'{'type=''{0}'', geometries={1}'}'", type, geometries);
    }

    /**
     * Compares this {@code GeometryCollection} to the specified object for equality.
     * <p>
     * Returns {@code true} if and only if the specified object is also a
     * {@code GeometryCollection} with the same {@code type} and an equal list of {@code geometries}.
     *
     * @param o the object to compare with this {@code GeometryCollection}
     * @return {@code true} if the specified object is equal to this {@code GeometryCollection}, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeometryCollection that)) return false;

        return type.equals(that.type) && geometries.equals(that.geometries);
    }

    /**
     * Computes the hash code for this {@code GeometryCollection}.
     * <p>
     * The hash code is calculated using the hash codes of the {@code type}
     * and the list of {@code geometries}, combined in a way to reduce hash collisions.
     *
     * @return the hash code value for this {@code GeometryCollection}
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + geometries.hashCode();
        return result;
    }
}
