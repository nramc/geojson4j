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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.nramc.geojson.constant.GeoJsonType.GEOMETRY_COLLECTION;
import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POLYGON;
import static com.github.nramc.geojson.constant.GeoJsonType.POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;
import static org.assertj.core.api.Assertions.assertThat;

class GeometryTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization_withPoint() throws IOException {
        String json = """
                { "type": "Point", "coordinates": [100.0, 0.0] }""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(POINT))
                .isInstanceOf(Point.class);
    }

    @Test
    void deserialization_withMultiPoint() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] }""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_POINT))
                .isInstanceOf(MultiPoint.class);
    }

    @Test
    void deserialization_withLineString() throws IOException {
        String json = """
                { "type": "LineString", "coordinates": [ [101.0, 0.0], [102.0, 1.0] ] }""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(LINE_STRING))
                .isInstanceOf(LineString.class);
    }

    @Test
    void deserialization_withMultiLineString() throws IOException {
        String json = """
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ] ] }""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_LINE_STRING))
                .isInstanceOf(MultiLineString.class);
    }

    @Test
    void deserialization_withPolygonAndWithoutHoles() throws IOException {
        String json = """
                {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]}""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(POLYGON))
                .isInstanceOf(Polygon.class);
    }

    @Test
    void deserialization_withPolygonAndWithHoles() throws IOException {
        String json = """
                { "type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]] }""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(POLYGON))
                .isInstanceOf(Polygon.class);
    }

    @Test
    void deserialization_withMultiPolygon() throws IOException {
        String json = """
                {"type": "MultiPolygon", "coordinates": [[[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_POLYGON))
                .isInstanceOf(MultiPolygon.class);
    }

    @Test
    void deserialization_withGeometryCollection() throws IOException {
        String json = """
                {
                  "type": "GeometryCollection",
                  "geometries": [
                    {"type": "Point", "coordinates": [100.0, 0.0]},
                    {"type": "LineString", "coordinates": [[101.0, 0.0], [102.0, 1.0]]},
                    {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]},
                    {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]]},
                    {"type": "MultiPoint", "coordinates": [[100.0, 0.0], [101.0, 1.0]]},
                    {"type": "MultiLineString", "coordinates": [[[100.0, 0.0], [101.0, 1.0]], [[102.0, 2.0], [103.0, 3.0]]]},
                    {"type": "MultiPolygon", "coordinates": [[[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}
                  ]
                }""";
        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GEOMETRY_COLLECTION))
                .isInstanceOf(GeometryCollection.class);
    }

}
