package com.github.nramc.geojson.domain;

import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationResult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Position implements Validatable, Serializable {

    private final double[] coordinates;

    public Position(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public static Position of(double[] coordinates) {
        return new Position(coordinates);
    }

    public static Position of(double longitude, double latitude) {
        return new Position(new double[]{longitude, latitude});
    }

    public static Position of(double longitude, double latitude, double altitude) {
        return new Position(new double[]{longitude, latitude, altitude});
    }

    public double[] getCoordinates() {
        return this.coordinates;
    }

    public double getLongitude() {
        return coordinates.length > 0 ? coordinates[0] : Double.NaN;
    }

    public double getLatitude() {
        return coordinates.length > 1 ? coordinates[1] : Double.NaN;
    }

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

    @Override
    public boolean isValid() {
        return !validate().hasErrors();
    }

    @Override
    public ValidationResult validate() {
        Set<String> errors = new HashSet<>();
        if (!isLengthValid(coordinates)) {
            errors.add("coordinates.length.invalid");
        } else if (isLongitudeValid(coordinates[0])) {
            errors.add("coordinates.longitude.invalid");
        } else if (isLatitudeValid(coordinates[1])) {
            errors.add("coordinates.latitude.invalid");
        }
        return new ValidationResult(errors);
    }
}
