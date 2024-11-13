package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POINT;

/**
 * Represents a GeoJSON MultiPoint geometry object.
 * <p>
 * A MultiPoint object contains multiple Position objects, which represent individual points in 2D or 3D space.
 * The MultiPoint class validates the coordinates and ensures they meet the expected criteria for GeoJSON format.
 * </p>
 */
public final class MultiPoint extends Geometry {
    private final String type;
    private final List<Position> coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public MultiPoint() {
        this(null, null);
    }

    /**
     * Creates a MultiPoint object with the specified type and coordinates.
     * This constructor is typically used for deserialization of JSON data.
     * <p>
     * The coordinates list is wrapped in an unmodifiable list to ensure immutability.
     * </p>
     *
     * @param type        The type of the geometry, typically "MultiPoint".
     * @param coordinates The list of positions that make up the MultiPoint.
     */
    @JsonCreator
    public MultiPoint(String type, List<Position> coordinates) {
        this.type = type;
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    /**
     * Factory method to create a MultiPoint instance from a list of coordinates.
     *
     * @param coordinates A list of Position objects representing the coordinates.
     * @return A new MultiPoint instance with the given coordinates.
     * @throws com.github.nramc.geojson.validator.GeoJsonValidationException if the provided coordinates are invalid.
     */
    public static MultiPoint of(List<Position> coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiPoint(MULTI_POINT, coordinates));
    }

    /**
     * Factory method to create a MultiPoint instance from a varargs array of Position objects.
     *
     * @param positions A varargs array of Position objects representing the coordinates.
     * @return A new MultiPoint instance with the given coordinates.
     * @throws com.github.nramc.geojson.validator.GeoJsonValidationException if the provided coordinates are invalid.
     */
    public static MultiPoint of(Position... positions) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiPoint(MULTI_POINT, Arrays.stream(positions).toList()));
    }

    /**
     * Gets the type of the geometry.
     *
     * @return The type of the geometry, which is "MultiPoint" for this class.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the list of coordinates (Position objects) that make up the MultiPoint.
     *
     * @return An unmodifiable list of Position objects.
     */
    public List<Position> getCoordinates() {
        return coordinates;
    }

    /**
     * Validates the MultiPoint geometry.
     * <p>
     * The validation checks the following:
     * <ul>
     *     <li>Validates that the type is "MultiPoint".</li>
     *     <li>Ensures the coordinates are not empty or null.</li>
     *     <li>Checks that there are at least 2 coordinates.</li>
     *     <li>Validates each Position in the coordinates list.</li>
     * </ul>
     * </p>
     *
     * @return A ValidationResult object containing any validation errors.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, MULTI_POINT)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, MULTI_POINT), "type.invalid"));
        }
        if (CollectionUtils.isEmpty(coordinates)) {
            errors.add(ValidationError.of("coordinates", "coordinates should not be empty/blank", "coordinates.invalid.empty"));
        }
        if (CollectionUtils.size(coordinates) < 2) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, minimum 2 positions required", "coordinates.invalid.min.length"));
        }

        CollectionUtils.emptyIfNull(coordinates).stream().map(Position::validate)
                .filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }


}
