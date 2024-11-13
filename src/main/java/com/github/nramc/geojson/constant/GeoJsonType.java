package com.github.nramc.geojson.constant;

/**
 * A utility class that provides constant string values representing different
 * GeoJSON types as defined in the GeoJSON specification (RFC 7946).
 * <p>
 * This class contains predefined constants for various GeoJSON object types,
 * such as {@code Point}, {@code LineString}, {@code Polygon}, etc. It cannot
 * be instantiated and is intended for use as a source of constant values.
 * </p>
 */
public final class GeoJsonType {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link IllegalStateException} if called.
     */
    private GeoJsonType() {
        throw new IllegalStateException("Utility class");
    }

    public static final String POINT = "Point";
    public static final String MULTI_POINT = "MultiPoint";
    public static final String LINE_STRING = "LineString";
    public static final String MULTI_LINE_STRING = "MultiLineString";
    public static final String POLYGON = "Polygon";
    public static final String MULTI_POLYGON = "MultiPolygon";
    public static final String GEOMETRY_COLLECTION = "GeometryCollection";
    public static final String FEATURE = "Feature";
    public static final String FEATURE_COLLECTION = "FeatureCollection";
}
