package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a GeoJSON Point, defined by a single geographical position.
 * <p>
 * A Point in GeoJSON consists of a single coordinate pair (latitude, longitude)
 * and optionally an altitude. This class provides methods to access these
 * coordinates and ensures they adhere to the valid ranges defined in the GeoJSON
 * specification (RFC 7946).
 * </p>
 *
 * <ol>
 *     <li>The longitude of the point, ranging from -180 to 180 degrees.</li>
 *     <li>The latitude of the point, ranging from -90 to 90 degrees.</li>
 *     <li>The altitude of the point, there is no strict range validation, but usually ranging from -11000 to 11000 meters.</li>
 * </ol>>
 *
 * <p>Example: {@code new Point(37.7749, -122.4194)}</p>
 */
public class Position implements Validatable, Serializable {

    @JsonValue
    private final double[] coordinates;

    /**
     * Constructs a Point with empty latitude, longitude and altitude.
     * Mainly useful for An Object-Relational Mapping(ORM) tools which does not support parameterized constructor to persist object into repository.
     */
    public Position() {
        this.coordinates = new double[2];
    }

    /**
     * Constructs a Point with the given latitude, longitude and optional altitude.
     * Does not perform validation eagerly.
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
        return validateAndThrowErrorIfInvalid(new Position(coordinates));
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
        return validateAndThrowErrorIfInvalid(new Position(new double[]{longitude, latitude}));
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
        return validateAndThrowErrorIfInvalid(new Position(new double[]{longitude, latitude, altitude}));
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
     *     <li>Ensures that the length of the coordinates array is either 2(longitude & latitude) or 3(longitude, latitude & altitude).</li>
     *     <li>Validates the longitude value to ensure it is within the acceptable range between -180 and 180, both inclusive.</li>
     *     <li>Validates the latitude value to ensure it is within the acceptable range between -90 and 90, both inclusive.</li>
     * </ul>
     *
     * <p>If any of these validations fail, the corresponding error keys are added to a set of
     * errors, which is then wrapped in a {@code ValidationResult} object and returned.</p>
     *
     * @return A {@code ValidationResult} object containing any validation errors found. If there
     * are no errors, the returned {@code ValidationResult} will contain an empty set of errors {@code ValidationError}.
     * @see ValidationResult
     * @see ValidationError
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (!isLengthValid(getCoordinates())) {
            errors.add(ValidationError.of("coordinates", "coordinates length is not valid", "coordinates.length.invalid"));
        } else if (!isLongitudeValid(getLongitude())) {
            errors.add(ValidationError.of("longitude", "longitude is not valid", "coordinates.longitude.invalid"));
        } else if (!isLatitudeValid(getLatitude())) {
            errors.add(ValidationError.of("latitude", "latitude is not valid", "coordinates.latitude.invalid"));
        }
        return new ValidationResult(errors);
    }

    private static Position validateAndThrowErrorIfInvalid(Position position) {
        ValidationResult validationResult = position.validate();
        if (validationResult.hasErrors()) {
            throw new GeoJsonValidationException("GeoJson Invalid", validationResult.getErrors());
        }
        return position;
    }
}
