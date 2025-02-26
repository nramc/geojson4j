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
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE_COLLECTION;


/**
 * Represents a GeoJSON FeatureCollection object, which is a collection of {@link Feature} objects.
 * <p>
 * A FeatureCollection is an object that contains an array of features, each representing a geographic
 * feature with a geometry and properties. The type of the FeatureCollection should always be "FeatureCollection",
 * as specified by the GeoJSON standard.
 * </p>
 * <p>
 * This class implements the {@link Validatable} interface to support validation of the feature collection
 * according to the GeoJSON specification. It is immutable and thread-safe.
 * </p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.3">RFC 7946 - Section 3.3</a>
 * </p>
 */
public final class FeatureCollection extends GeoJson implements Validatable {
    private final List<Feature> features;

    /**
     * Default constructor required for frameworks (e.g., serialization).
     * <p>
     * This constructor does not perform any validation. After using this constructor, it is recommended to
     * call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public FeatureCollection() {
        this(FEATURE_COLLECTION, null);
    }

    /**
     * Constructs a FeatureCollection with the specified type and list of features.
     * <p>
     * The type should be set to "FeatureCollection", as per the GeoJSON specification.
     * </p>
     *
     * @param type     The type of the collection. It should be "FeatureCollection".
     * @param features A list of {@link Feature} objects that represent the features of this collection.
     *                 The list can be empty, but it cannot be null.
     */
    @JsonCreator
    public FeatureCollection(@JsonProperty("type") String type, @JsonProperty("features") List<Feature> features) {
        super(type);
        this.features = CollectionUtils.emptyIfNull(features).stream().toList();
    }

    /**
     * Factory method to create a validated {@link FeatureCollection} with the given list of features.
     * <p>
     * This method validates the created FeatureCollection using {@link ValidationUtils#validateAndThrowErrorIfInvalid(Validatable)}.
     * If validation fails, it throws a {@link GeoJsonValidationException} with details about the validation errors.
     * </p>
     *
     * @param features A list of {@link Feature} objects that represent the features of this collection.
     *                 Cannot be null or empty.
     * @return A validated {@link FeatureCollection} object.
     * @throws GeoJsonValidationException if the FeatureCollection is invalid according to GeoJSON validation rules.
     */
    public static FeatureCollection of(List<Feature> features) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new FeatureCollection(FEATURE_COLLECTION, features));
    }

    /**
     * Factory method to create a validated {@link FeatureCollection} with the given {@link Feature} objects.
     * <p>
     * This method is a convenience for creating a collection from an array of features.
     * </p>
     *
     * @param features The {@link Feature} objects to include in the collection.
     * @return A validated {@link FeatureCollection} object.
     * @throws GeoJsonValidationException if the FeatureCollection is invalid according to GeoJSON validation rules.
     */
    public static FeatureCollection of(Feature... features) {
        return of(List.of(features));
    }

    /**
     * Gets the type of the FeatureCollection.
     * <p>
     * The type is always "FeatureCollection" as per the GeoJSON specification.
     * </p>
     *
     * @return The type of the feature collection, which is always "FeatureCollection".
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Gets the list of features in this FeatureCollection.
     * <p>
     * The list contains all {@link Feature} objects that are part of this collection.
     * </p>
     *
     * @return An unmodifiable list of {@link Feature} objects.
     */
    public List<Feature> getFeatures() {
        return features;
    }

    /**
     * Validates the FeatureCollection according to the GeoJSON specification.
     * <p>
     * The validation checks include:
     * <ul>
     *     <li>The type must be "FeatureCollection".</li>
     *     <li>Each feature in the collection is validated using {@link Feature#validate()}.</li>
     * </ul>
     * </p>
     *
     * @return A {@link ValidationResult} containing any validation errors, or an empty result if valid.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, FEATURE_COLLECTION)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, FEATURE_COLLECTION), "type.invalid"));
        }

        CollectionUtils.emptyIfNull(features).stream()
                .map(Validatable::validate)
                .filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Returns a string representation of this FeatureCollection.
     *
     * <p>The string includes the {@code type} and {@code features} fields,
     * formatted in a readable manner for debugging and logging purposes.</p>
     *
     * @return a string representation of this FeatureCollection.
     */
    @Override
    public String toString() {
        return MessageFormat.format("FeatureCollection'{'type=''{0}'', features={1}'}'", type, features);
    }

    /**
     * Compares this FeatureCollection to the specified object for equality.
     *
     * <p>The comparison checks whether the {@code type} and {@code features}
     * fields are equal between this FeatureCollection and the specified object.</p>
     *
     * @param o the object to compare with this FeatureCollection.
     * @return {@code true} if the specified object is equal to this FeatureCollection; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeatureCollection that)) {
            return false;
        }

        return Objects.equals(type, that.type) && Objects.equals(features, that.features);
    }

    /**
     * Computes the hash code for this FeatureCollection.
     *
     * <p>The hash code is calculated using the {@code type} and {@code features}
     * fields. It ensures consistent hashing for objects that are equal according
     * to the {@link #equals(Object)} method.</p>
     *
     * @return the hash code value for this FeatureCollection.
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(features);
        return result;
    }
}
