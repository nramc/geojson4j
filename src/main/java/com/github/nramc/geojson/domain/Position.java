package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationResult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Position implements Validatable, Serializable {

    @JsonValue
    private final double[] coordinates;

    @JsonCreator
    public Position(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public static Position of(double[] coordinates) {
        return validateAndThrowErrorIfInvalid(new Position(coordinates));
    }

    public static Position of(double longitude, double latitude) {
        return validateAndThrowErrorIfInvalid(new Position(new double[]{longitude, latitude}));
    }

    public static Position of(double longitude, double latitude, double altitude) {
        return validateAndThrowErrorIfInvalid(new Position(new double[]{longitude, latitude, altitude}));
    }

    @JsonIgnore
    public double[] getCoordinates() {
        return this.coordinates;
    }

    @JsonIgnore
    public double getLongitude() {
        return coordinates.length > 0 ? coordinates[0] : Double.NaN;
    }

    @JsonIgnore
    public double getLatitude() {
        return coordinates.length > 1 ? coordinates[1] : Double.NaN;
    }

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

    @JsonIgnore
    @Override
    public boolean isValid() {
        return !validate().hasErrors();
    }

    @Override
    public ValidationResult validate() {
        Set<String> errors = new HashSet<>();
        if (!isLengthValid(getCoordinates())) {
            errors.add("coordinates.length.invalid");
        } else if (!isLongitudeValid(getLongitude())) {
            errors.add("coordinates.longitude.invalid");
        } else if (!isLatitudeValid(getLatitude())) {
            errors.add("coordinates.latitude.invalid");
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
