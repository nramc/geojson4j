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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;

/**
 * Represents a GeoJSON Point, defined by a single geographical position.
 *
 * <p>A Point in GeoJSON consists of a single coordinate pair (latitude, longitude)
 * and optionally an altitude. This class provides methods to access these
 * coordinates and ensures they adhere to the valid ranges defined in the GeoJSON
 * specification (RFC 7946).
 * </p>
 *
 * <ol>
 *     <li>The longitude of the point, ranging from -180 to 180 degrees.</li>
 *     <li>The latitude of the point, ranging from -90 to 90 degrees.</li>
 *     <li>The altitude of the point, there is no strict range validation, but usually ranging from -11000 to 11000 meters.</li>
 * </ol>
 *
 * <p>Example usage:
 * <pre>{@code
 * Position position = Position.of(40.7128, -74.0060);
 * }</pre></p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.1">RFC 7946 - Section 3.1.1</a></p>
 */
public class Position implements Validatable, Serializable {

    @JsonValue
    private final double[] coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     */
    public Position() {
        this.coordinates = new double[]{Double.NaN, Double.NaN};
    }

    /**
     * Constructs a Point with the given latitude, longitude and optional altitude.
     * Does not perform validation eagerly.
     * This constructor is typically used for deserialization of JSON data.
     *
     * <p>If object created using constructor directly, then use below option for validation,</p>
     * <ol>
     *     <li>{@link Position#validate()} to perform validation and return result</li>
     *     <li>{@link Position#isValid()} to check whether GeoJson valid or not.</li>
     * </ol>
     *
     * @param coordinates [longitude, latitude, altitude]
     */
    @JsonCreator
    public Position(double[] coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Validate the given latitude, longitude and optional altitude, Constructs a Point only when given coordinates valid.
     * Otherwise, throws {@link GeoJsonValidationException} with validation errors.
     *
     * @param coordinates [longitude, latitude, altitude]
     * @throws GeoJsonValidationException with validation errors
     */
    public static Position of(double[] coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Position(coordinates));
    }

    /**
     * Validate the given latitude and longitude, Constructs a Point only when given coordinates valid.
     * Otherwise, throws {@link GeoJsonValidationException} with validation errors.
     *
     * @param longitude The longitude of the point
     * @param latitude  The latitude of the point
     * @throws GeoJsonValidationException with validation errors
     */
    public static Position of(double longitude, double latitude) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Position(new double[]{longitude, latitude}));
    }

    /**
     * Validate the given latitude, longitude and altitude, Constructs a Point only when given coordinates valid.
     * Otherwise, throws {@link GeoJsonValidationException} with validation errors.
     *
     * @param longitude The longitude of the point
     * @param latitude  The latitude of the point
     * @param altitude  The altitude of the point
     * @throws GeoJsonValidationException with validation errors
     */
    public static Position of(double longitude, double latitude, double altitude) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new Position(new double[]{longitude, latitude, altitude}));
    }

    /**
     * Returns the coordinates of the point, contains latitude, longitude and optional altitude.
     *
     * @return The [longitude, latitude, altitude]
     */
    @JsonIgnore
    public double[] getCoordinates() {
        return this.coordinates;
    }

    /**
     * Returns the longitude of the point.
     *
     * @return The longitude, ranging from -180 to 180 degrees.
     */
    @JsonIgnore
    public double getLongitude() {
        return coordinates.length > 0 ? coordinates[0] : Double.NaN;
    }

    /**
     * Returns the latitude of the point.
     *
     * @return The latitude, ranging from -90 to 90 degrees.
     */
    @JsonIgnore
    public double getLatitude() {
        return coordinates.length > 1 ? coordinates[1] : Double.NaN;
    }

    /**
     * Returns the altitude of the point.
     *
     * @return The altitude, usually ranging from -11000 to 11000 meters, but no guaranty for the range.
     */
    @JsonIgnore
    public double getAltitude() {
        return coordinates.length > 2 ? coordinates[2] : Double.NaN;
    }

    private static boolean isLengthValid(double[] coordinates) {
        // Position can have minimum 2 and maximum 3 values only
        return coordinates.length == 2 || coordinates.length == 3;
    }

    private static boolean isLongitudeValid(double longitude) {
        //Valid longitude values are between -180 and 180, both inclusive.
        return longitude >= -180 && longitude <= 180;
    }

    private static boolean isLatitudeValid(double latitude) {
        // Valid latitude values are between -90 and 90, both inclusive.
        return latitude >= -90 && latitude <= 90;
    }

    /**
     * Validates the coordinates of the current GeoJSON object and checks if they conform to
     * the expected ranges and lengths. The method performs several checks:
     * <ul>
     *     <li>Ensures that the length of the coordinates array is either 2(longitude and latitude) or 3(longitude, latitude and altitude).</li>
     *     <li>Validates the longitude value to ensure it is within the acceptable range between -180 and 180, both inclusive.</li>
     *     <li>Validates the latitude value to ensure it is within the acceptable range between -90 and 90, both inclusive.</li>
     * </ul>
     *
     * <p>If any of these validations fail, the corresponding error keys are added to a set of
     * errors, which is then wrapped in a {@code ValidationResult} object and returned.</p>
     *
     * @return A {@code ValidationResult} result of validation process.
     * @see ValidationResult
     * @see ValidationError
     */
    @Override
    @SuppressWarnings("java:S1192") // String literals should not be duplicated
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (!isLengthValid(getCoordinates())) {
            errors.add(ValidationError.of("coordinates", "coordinates length is not valid", "coordinates.length.invalid"));
        } else if (!isLongitudeValid(getLongitude())) {
            errors.add(ValidationError.of("coordinates", "longitude is not valid", "coordinates.longitude.invalid"));
        } else if (!isLatitudeValid(getLatitude())) {
            errors.add(ValidationError.of("coordinates", "latitude is not valid", "coordinates.latitude.invalid"));
        }
        return new ValidationResult(errors);
    }

    /**
     * Returns a string representation of the coordinates array.
     * The format of the output is similar to the standard array representation,
     * provided by {@link Arrays#toString(Object[])}.
     *
     * @return A string representation of the coordinates array.
     */
    @Override
    public String toString() {
        return Arrays.toString(coordinates);
    }

    /**
     * Checks if this {@code Position} is equal to the given object based on coordinate values.
     *
     * @param o The object to compare.
     * @return {@code true} if both are {@code Position} objects with equal coordinates; otherwise, {@code false}.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position position)) {
            return false;
        }

        return Arrays.equals(coordinates, position.coordinates);
    }

    /**
     * Returns the hash code based on the coordinates.
     *
     * @return The hash code of the coordinates array.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates);
    }
}
