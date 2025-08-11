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

import static com.github.nramc.geojson.constant.GeoJsonType.GEOMETRY_COLLECTION;
import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POLYGON;
import static com.github.nramc.geojson.constant.GeoJsonType.POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.nramc.geojson.validator.Validatable;


/**
 * The {@code Geometry} class is an abstract base class representing different geometric shapes
 * that conform to the GeoJSON specification. This class is a direct subclass of {@link GeoJson}
 * and serves as a parent for various specific geometry types like {@code Point}, {@code MultiPoint},
 * {@code LineString}, {@code MultiLineString}, {@code Polygon}, {@code MultiPolygon}, and
 * {@code GeometryCollection}.
 *
 * <p>Each subclass of {@code Geometry} represents a different type of geometric structure, and
 * the polymorphic behavior is managed using Jackson annotations for seamless serialization and
 * deserialization.</p>
 *
 * <p>Jackson Annotations:</p>
 * <ul>
 *   <li>{@code @JsonTypeInfo}: This annotation is used to specify how type information should be
 *   included in the serialized JSON. The {@code property} parameter specifies that the
 *   {@code "type"} field will be used to identify the specific subclass of {@code Geometry}
 *   during deserialization.</li>
 *   <li>{@code @JsonSubTypes}: This annotation lists all possible subclasses of {@code Geometry}
 *   and maps them to their respective type names. This enables Jackson to correctly handle
 *   polymorphic deserialization.</li>
 * </ul>
 *
 * <strong>Inheritance:</strong>
 *
 * <p>This class is declared as a sealed class, meaning it explicitly defines which subclasses
 * are permitted to extend it. This provides better control over the class hierarchy and ensures
 * that only specific types of geometries can be represented.
 * </p>
 *
 * <p>GeoJSON Specification Reference:
 * <a href="https://datatracker.ietf.org/doc/html/rfc7946#section-3.1">RFC 7946 - Section 3.1</a>
 * </p>
 *
 * @see GeoJson
 * @see Point
 * @see MultiPoint
 * @see LineString
 * @see MultiLineString
 * @see Polygon
 * @see MultiPolygon
 * @see GeometryCollection
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = POINT),
        @JsonSubTypes.Type(value = MultiPoint.class, name = MULTI_POINT),
        @JsonSubTypes.Type(value = LineString.class, name = LINE_STRING),
        @JsonSubTypes.Type(value = MultiLineString.class, name = MULTI_LINE_STRING),
        @JsonSubTypes.Type(value = Polygon.class, name = POLYGON),
        @JsonSubTypes.Type(value = MultiPolygon.class, name = MULTI_POLYGON),
        @JsonSubTypes.Type(value = GeometryCollection.class, name = GEOMETRY_COLLECTION)
})
public abstract sealed class Geometry extends GeoJson implements Validatable permits
        Point, MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon, GeometryCollection {

    protected Geometry() {
        super();
    }

    protected Geometry(String type) {
        super(type);
    }
}
