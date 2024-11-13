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
