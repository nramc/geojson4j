package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import com.github.nramc.geojson.validator.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;

/**
 * A class representing a GeoJSON MultiLineString geometry object.
 * A MultiLineString is a collection of {@link LineString} objects, each of which consists of two or more {@link Position} coordinates.
 * The MultiLineString object allows for the representation of multiple lines.
 */
public final class MultiLineString extends Geometry {
    private final String type;
    private final List<List<Position>> coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks)
     * and serialization mechanisms that need to instantiate objects without arguments.
     * <p>
     * This constructor does not perform any validation. After using this constructor,
     * it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     */
    public MultiLineString() {
        this(null, null);
    }

    /**
     * Constructs a new {@link MultiLineString} object with the specified type and coordinates.
     * This constructor is typically used for deserialization of JSON data.
     *
     * @param type        The type of GeoJSON geometry, expected to be "MultiLineString".
     * @param coordinates The list of coordinates, where each element is a list of {@link Position} objects representing a LineString.
     */
    @JsonCreator
    public MultiLineString(String type, List<List<Position>> coordinates) {
        this.type = type;
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    /**
     * Creates a new {@link MultiLineString} with the specified coordinates.
     * The coordinates are validated before the MultiLineString object is created.
     *
     * @param coordinates A list of lists of {@link Position} objects representing multiple LineString coordinates.
     * @return A new {@link MultiLineString} object if the coordinates are valid.
     * @throws GeoJsonValidationException If validation of the coordinates fails.
     */
    public static MultiLineString of(List<List<Position>> coordinates) throws GeoJsonValidationException {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiLineString(MULTI_LINE_STRING, coordinates));
    }

    /**
     * Creates a new {@link MultiLineString} with the specified coordinates.
     * This method accepts a varargs array of {@link Position} lists representing multiple LineStrings.
     * The coordinates are validated before the MultiLineString object is created.
     *
     * @param coordinates A varargs array of {@link Position} lists representing multiple LineString coordinates.
     * @return A new {@link MultiLineString} object if the coordinates are valid.
     * @throws GeoJsonValidationException If validation of the coordinates fails.
     */
    @SafeVarargs
    public static MultiLineString of(List<Position>... coordinates) {
        return ValidationUtils.validateAndThrowErrorIfInvalid(new MultiLineString(MULTI_LINE_STRING, Arrays.stream(coordinates).toList()));
    }

    /**
     * Validates the MultiLineString object by checking its type and coordinates.
     * If any validation errors are found, they are added to a set of errors.
     *
     * @return A {@link ValidationResult} containing any validation errors.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        // Validate type
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, MULTI_LINE_STRING)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, MULTI_LINE_STRING), "type.invalid"));
        }

        // Validate coordinates
        if (CollectionUtils.isEmpty(coordinates)) {
            errors.add(ValidationError.of("coordinates", "coordinates should not be empty/blank", "coordinates.invalid.empty"));
        }

        // Validate each line in the coordinates
        if (coordinates.stream().anyMatch(positions -> positions.size() < 2)) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, minimum 2 positions required", "coordinates.invalid.min.length"));
        }

        // Validate each position in the coordinates
        coordinates.stream().flatMap(Collection::stream)
                .map(Position::validate).filter(ValidationResult::hasErrors)
                .map(ValidationResult::getErrors)
                .forEach(errors::addAll);

        return new ValidationResult(errors);
    }

    /**
     * Gets the type of the GeoJson.
     *
     * @return The type of the GeoJson, which is "MultiLineString".
     */
    @Override
    public String getType() {
        return type;
    }
}
