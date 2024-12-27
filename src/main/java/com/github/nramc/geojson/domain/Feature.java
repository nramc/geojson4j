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
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE;

/**
 * Represents a GeoJSON Feature object, which is a fundamental element in GeoJSON that contains
 * a geometry, an optional identifier (id), and optional properties.
 * <p>
 * A Feature in GeoJSON may represent a geographical feature, such as a point, line, or polygon,
 * and can also have properties (metadata) associated with it. The Feature also includes a type
 * and may have a unique identifier. This class implements the {@link Validatable} interface to
 * support validation according to the GeoJSON specification.
 * </p>
 * <p>
 * This class is immutable and thread-safe. All collections are unmodifiable.
 * </p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.2">RFC 7946 - Section 3.2</a>
 * </p>
 */
public non-sealed class Feature extends GeoJson implements Validatable, Serializable {
    private final String id;
    private final Geometry geometry;
    private final Map<String, Serializable> properties;

    /**
     * Default constructor required for frameworks (e.g., ORM frameworks or serialization mechanisms)
     * that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public Feature() {
        this(FEATURE, null, null, null);
    }

    /**
     * Constructs a Feature object with the specified type, id, geometry, and properties.
     * <p>
     * This constructor is used when creating a Feature with a specified type, id, geometry, and properties.
     * The properties are copied to ensure immutability.
     * </p>
     *
     * @param type       The type of the feature. It should be "Feature" as per the GeoJSON specification.
     * @param id         The identifier for the feature. Can be null if no identifier is provided.
     * @param geometry   The geometry representing the feature's shape. Cannot be null.
     * @param properties A map of properties associated with the feature. Can be empty or null.
     */
    @JsonCreator
    public Feature(
            @JsonProperty("type") String type,
            @JsonProperty("id") String id,
            @JsonProperty("geometry") Geometry geometry,
            @JsonProperty("properties") Map<String, Serializable> properties) {
        super(type);
        this.id = id;
        this.geometry = geometry;
        this.properties = Map.copyOf(properties);
    }

    /**
     * Factory method to create a validated {@link Feature} with the given id, geometry, and properties.
     * <p>
     * This method validates the created Feature using {@link ValidationUtils#validateAndThrowErrorIfInvalid(Validatable)}.
     * If validation fails, it throws a {@link GeoJsonValidationException} with details about the validation errors.
     * </p>
     *
     * @param id         The identifier for the feature. Can be null if no identifier is provided.
     * @param geometry   The geometry representing the feature's shape. Cannot be null.
     * @param properties A map of properties associated with the feature. Can be empty or null.
     * @return A validated {@link Feature} object.
     * @throws GeoJsonValidationException if the Feature is invalid according to GeoJSON validation rules.
     */
    public static Feature of(String id, Geometry geometry, Map<String, Serializable> properties) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Feature(GeoJsonType.FEATURE, id, geometry, MapUtils.emptyIfNull(properties)));
    }

    /**
     * Gets the type of the Feature.
     *
     * @return The type of the feature, which is "Feature".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Gets the properties associated with the Feature.
     * <p>
     * These properties are stored in a map where the key is the property name, and the value is the property value.
     * The map is unmodifiable.
     * </p>
     *
     * @return An unmodifiable map of properties associated with the feature.
     */
    public Map<String, Serializable> getProperties() {
        return properties;
    }

    /**
     * Retrieves a property by its name.
     *
     * @param property The name of the property to retrieve.
     * @return The value of the property, or null if the property does not exist.
     */
    public Serializable getProperty(String property) {
        return properties.get(property);
    }

    /**
     * Retrieves a property by its name, wrapped in an Optional.
     * <p>
     * If the property does not exist, an empty {@link Optional} will be returned.
     * </p>
     *
     * @param property The name of the property to retrieve.
     * @return An {@link Optional} containing the property value, or an empty Optional if the property does not exist.
     */
    public Optional<Serializable> getPropertyIfExists(String property) {
        return Optional.ofNullable(properties.getOrDefault(property, null));
    }

    /**
     * Gets the geometry representing the feature's shape.
     * <p>
     * The geometry defines the shape of the feature (e.g., Point, LineString, Polygon).
     * </p>
     *
     * @return The geometry of the feature.
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Gets the identifier for the Feature.
     * <p>
     * The id is optional in GeoJSON and can be null if no identifier is provided.
     * </p>
     *
     * @return The id of the feature, or null if not provided.
     */
    public String getId() {
        return id;
    }

    /**
     * Validates the Feature according to the GeoJSON specification.
     * <p>
     * The validation checks include:
     * <ul>
     *     <li>The type must be "Feature".</li>
     *     <li>The geometry must not be null.</li>
     *     <li>The geometry must be valid, as determined by the {@link Geometry#validate()} method.</li>
     * </ul>
     * </p>
     *
     * @return A {@link ValidationResult} containing any validation errors, or an empty result if valid.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, FEATURE)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, FEATURE), "type.invalid"));
        }
        if (geometry == null) {
            errors.add(ValidationError.of("geometry", "geometry should not be empty/blank", "geometry.invalid.empty"));
        }
        Optional.ofNullable(geometry).map(Geometry::validate)
                .filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .ifPresent(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Returns a string representation of the Feature object.
     *
     * <p>The returned string includes the type, id, geometry, and properties
     * of the feature in a formatted manner. This can be useful for debugging
     * or logging purposes.</p>
     *
     * @return a string representation of the Feature object, including its
     * type, id, geometry, and properties.
     */
    @Override
    public String toString() {
        return MessageFormat.format("Feature'{'type=''{0}'', id=''{1}'', geometry={2}, properties={3}'}'", type, id, geometry, properties);
    }

    /**
     * Compares this Feature object with another for equality.
     *
     * <p>The comparison checks if the given object is also a Feature and
     * verifies that the {@code type}, {@code id}, {@code geometry}, and
     * {@code properties} fields are equal.</p>
     *
     * @param o the object to compare with this Feature.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Feature feature)) {
            return false;
        }

        return type.equals(feature.type) && Objects.equals(id, feature.id) && geometry.equals(feature.geometry) && properties.equals(feature.properties);
    }

    /**
     * Computes the hash code for this Feature object.
     *
     * <p>The hash code is calculated using the {@code type}, {@code id},
     * {@code geometry}, and {@code properties} fields, ensuring consistency
     * with the {@link #equals(Object)} method.</p>
     *
     * @return the computed hash code for this Feature object.
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + Objects.hashCode(id);
        result = 31 * result + geometry.hashCode();
        result = 31 * result + properties.hashCode();
        return result;
    }
}
