package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nramc.geojson.validator.Validatable;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the coordinates of a Polygon in GeoJSON format.
 * <p>
 * A Polygon is defined by a series of linear rings, where the first ring is the exterior of the polygon,
 * and any subsequent rings represent holes (interior regions) within the polygon.
 * The exterior and holes are validated to ensure they adhere to the rules of GeoJSON polygons:
 * - The exterior must be a closed ring with at least 4 positions.
 * - Each hole must also be a closed ring, and can be empty if no holes exist.
 * </p>
 * <p>
 * This class is designed to be used with frameworks that serialize and deserialize GeoJSON objects, such as
 * Jackson for JSON parsing and ORM frameworks for persistence. The object can be validated using the {@link #validate()} method
 * to ensure its integrity according to GeoJSON standards.
 * </p>
 *
 * <p>Usage Example:</p>
 * <pre>
 * List<Position> exterior = Arrays.asList(Position.of(0, 0), Position.of(0, 1), Position.of(1, 1), Position.of(1, 0), Position.of(0, 0));
 * List<Position> hole = Arrays.asList(Position.of(0.2, 0.2), Position.of(0.2, 0.8), Position.of(0.8, 0.8), Position.of(0.8, 0.2), Position.of(0.2, 0.2));
 * PolygonCoordinates polygon = PolygonCoordinates.of(exterior, List.of(hole));
 * </pre>
 *
 * <p>
 * The `validate()` method can be called to check whether the polygon coordinates are valid:
 * </p>
 * <pre>
 * ValidationResult validationResult = polygon.validate();
 * if (validationResult.hasErrors()) {
 *     // Handle validation errors
 * }
 * </pre>
 */
public class PolygonCoordinates implements Validatable, Serializable {
    private final List<Position> exterior;
    private final List<List<Position>> holes;

    /**
     * Default constructor. Initializes the PolygonCoordinates with null values.
     * <p>
     * This constructor is required for certain frameworks (e.g., ORM frameworks) and serialization mechanisms
     * that need to instantiate objects without arguments. It is recommended to call {@link #validate()} or
     * {@link #isValid()} method after using this constructor to ensure the object is in a valid state.
     * </p>
     */
    public PolygonCoordinates() {
        this(null, null);
    }

    /**
     * Constructs a PolygonCoordinates instance from a list of linear rings.
     * <p>
     * The first linear ring is assigned to the exterior of the polygon,
     * and the remaining rings are assigned to the holes.
     * </p>
     *
     * @param linearRings List of linear rings, where the first list is the exterior and the others are the holes.
     */
    @JsonCreator
    public PolygonCoordinates(final List<List<Position>> linearRings) {
        this(CollectionUtils.isNotEmpty(linearRings) ? linearRings.getFirst() : List.of(),
                linearRings.size() == 1 ? List.of() : linearRings.subList(1, linearRings.size()));
    }

    /**
     * Constructs a PolygonCoordinates instance with specified exterior and hole coordinates.
     *
     * @param exterior The exterior ring of the polygon.
     * @param holes    The hole rings inside the polygon.
     */
    public PolygonCoordinates(final List<Position> exterior, final List<List<Position>> holes) {
        this.exterior = Collections.unmodifiableList(exterior);
        this.holes = CollectionUtils.isNotEmpty(holes) ? Collections.unmodifiableList(holes) : null;
    }

    /**
     * Creates a PolygonCoordinates instance using a varargs of Position lists.
     * <p>
     * The first list in the varargs is the exterior ring, and the remaining lists are treated as holes.
     * </p>
     *
     * @param positions The position lists to construct the polygon coordinates.
     * @return A new PolygonCoordinates instance.
     */
    @SafeVarargs
    public static PolygonCoordinates of(List<Position>... positions) {
        return new PolygonCoordinates(Arrays.asList(positions));
    }

    /**
     * Creates a PolygonCoordinates instance from a list of linear rings.
     *
     * @param linearRings List of linear rings to be used as the coordinates.
     * @return A new PolygonCoordinates instance.
     */
    public static PolygonCoordinates of(List<List<Position>> linearRings) {
        return new PolygonCoordinates(linearRings);
    }

    /**
     * Returns the coordinates of the polygon, consisting of the exterior and holes.
     *
     * @return A list containing the exterior and hole coordinates.
     */
    @JsonValue
    public List<List<Position>> getCoordinates() {
        List<List<Position>> coordinates = new ArrayList<>();
        coordinates.add(this.exterior);
        if (CollectionUtils.isNotEmpty(this.holes)) {
            coordinates.addAll(this.holes);
        }
        return coordinates;
    }


    private static Set<ValidationError> validateLinerRing(List<Position> linearRing) {
        Set<ValidationError> errors = new HashSet<>();
        if (CollectionUtils.isEmpty(linearRing) || linearRing.size() < 4) {
            errors.add(ValidationError.of("coordinates", "Ring '%s' must contain at least four positions.".formatted(linearRing), "coordinates.length.invalid"));
        }
        if (!linearRing.getFirst().equals(linearRing.getLast())) {
            errors.add(ValidationError.of("coordinates", "Ring '%s', first and last position must be the same.".formatted(linearRing), "coordinates.cycle.invalid"));
        }
        return errors;
    }


    /**
     * Validates the polygon coordinates (exterior and holes).
     * <p>
     * This method checks that the exterior and holes are valid according to the rules for polygons:
     * - The exterior must be a valid linear ring (at least 4 positions, first equals last).
     * - Each hole must also be a valid linear ring.
     * </p>
     *
     * @return A {@link ValidationResult} containing any validation errors.
     */
    @Override
    public ValidationResult validate() {

        Set<ValidationError> errors = new HashSet<>(validateLinerRing(exterior));

        CollectionUtils.emptyIfNull(holes).stream().map(PolygonCoordinates::validateLinerRing)
                .filter(CollectionUtils::isNotEmpty).forEach(errors::addAll);

        return new ValidationResult(errors);
    }
}
