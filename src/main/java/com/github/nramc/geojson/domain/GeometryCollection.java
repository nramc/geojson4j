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
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
    public GeometryCollection(String type, List<Geometry> geometries) {
        this.type = type;
        this.geometries = List.copyOf(geometries);
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
}