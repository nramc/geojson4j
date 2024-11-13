package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;

/**
 * Represents a GeoJSON Polygon object, which is defined by a series of linear rings, including one exterior
 * ring and optionally several interior rings (holes). The {@link Polygon} class extends the {@link Geometry} base
 * class and provides methods for creating and validating Polygon objects.
 * <p>
 * A Polygon is structured according to the GeoJSON specification, where:
 * - The exterior ring defines the boundary of the polygon.
 * - Interior rings (holes) define regions within the polygon that are excluded from the area.
 * The {@link PolygonCoordinates} object is used to store and manage the rings.
 * </p>
 *
 * <p>
 * The Polygon object supports various factory methods to construct instances from exterior and interior rings.
 * </p>
 *
 * <p>
 * This class provides validation logic to ensure the Polygon's structure adheres to the GeoJSON specification.
 * </p>
 *
 * @see Geometry
 * @see PolygonCoordinates
 * @see Position
 */
public final class Polygon extends Geometry {
    private final String type;
    private final PolygonCoordinates coordinates;

    /**
     * No-argument constructor required for certain frameworks (e.g., ORM frameworks) and
     * serialization mechanisms that need to instantiate objects without arguments.
     */
    public Polygon() {
        this(null, null);
    }

    /**
     * Constructs a {@link Polygon} with the specified type and coordinates.
     * <p>
     * This constructor does not perform any validation. This constructor is typically used for deserialization of JSON data.
     * After using this constructor, it is recommended to call the {@link #validate()} or {@link #isValid()} method to ensure the object is in a valid state.
     * </p>
     *
     * @param type        The GeoJSON type, which should be "Polygon" for valid Polygon objects.
     * @param coordinates The {@link PolygonCoordinates} object representing the exterior and interior rings.
     */
    @JsonCreator
    public Polygon(final String type, final PolygonCoordinates coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    /**
     * Factory method to create a {@link Polygon} object using an exterior ring and a list of interior rings (holes).
     *
     * @param exterior The exterior ring of the polygon, represented as a list of {@link Position} objects.
     * @param holes    The interior rings (holes) of the polygon, represented as a list of lists of {@link Position} objects.
     * @return A validated {@link Polygon} object.
     */
    public static Polygon of(final List<Position> exterior, final List<List<Position>> holes) {
        return new Polygon(POLYGON, PolygonCoordinates.of(exterior, holes));
    }

    /**
     * Factory method to create a {@link Polygon} object using an exterior ring and a variable number of interior rings (holes).
     *
     * @param exterior The exterior ring of the polygon, represented as a list of {@link Position} objects.
     * @param holes    A variable number of interior rings (holes), each represented as a list of {@link Position} objects.
     * @return A validated {@link Polygon} object.
     */
    @SafeVarargs
    public final Polygon of(final List<Position> exterior, final List<Position>... holes) {
        return new Polygon(POLYGON, PolygonCoordinates.of(exterior, List.of(holes)));
    }

    /**
     * Factory method to create a {@link Polygon} object from a {@link PolygonCoordinates} instance.
     *
     * @param coordinates The {@link PolygonCoordinates} instance representing the polygon's exterior and interior rings.
     * @return A validated {@link Polygon} object.
     */
    public static Polygon of(PolygonCoordinates coordinates) {
        return new Polygon(POLYGON, coordinates);
    }

    /**
     * Factory method to create a {@link Polygon} object from a list of linear rings.
     *
     * @param coordinates The list of linear rings, where the first ring is the exterior and subsequent rings are interior holes.
     * @return A validated {@link Polygon} object.
     */
    public static Polygon of(List<List<Position>> coordinates) {
        return new Polygon(POLYGON, PolygonCoordinates.of(coordinates));
    }

    /**
     * Validates the {@link Polygon} object to ensure it adheres to the GeoJSON specification.
     * <p>
     * The validation checks include:
     * - Ensuring the "type" field is "Polygon".
     * - Ensuring the coordinates are not empty and contain at least one position.
     * - Delegating further validation to the {@link PolygonCoordinates} instance.
     * </p>
     *
     * @return A {@link ValidationResult} object containing any validation errors found.
     */
    @Override
    public ValidationResult validate() {
        Set<ValidationError> errors = new HashSet<>();
        if (StringUtils.isBlank(type) || !StringUtils.equals(type, POLYGON)) {
            errors.add(ValidationError.of("type", "type '%s' is not valid. expected '%s'".formatted(type, POLYGON), "type.invalid"));
        }

        if (CollectionUtils.size(coordinates) < 1) {
            errors.add(ValidationError.of("coordinates", "coordinates is not valid, at least one position required", "coordinates.invalid.min.length"));
        }
        ValidationResult coordinatesValidationResult = coordinates.validate();
        if (coordinatesValidationResult.hasErrors()) {
            errors.addAll(coordinatesValidationResult.getErrors());
        }
        return new ValidationResult(errors);
    }
}
