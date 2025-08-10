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
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.List;

import static com.github.nramc.geojson.constant.GeoJsonType.GEOMETRY_COLLECTION;
import static org.assertj.core.api.Assertions.assertThat;

class GeometryCollectionTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Point POINT = Point.of(100.0, 0.0);
    private static final LineString LINE_STRING = LineString.of(Position.of(101.0, 0.0), Position.of(102.0, 1.0));
    private static final Polygon POLYGON = Polygon.of(PolygonCoordinates.of(List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0))));
    private static final Polygon POLYGON_WITH_HOLES = Polygon.of(PolygonCoordinates.of(
            List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
            List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(100.8, 0.8))
    ));
    private static final MultiPoint MULTI_POINT = MultiPoint.of(Position.of(100.0, 0.0), Position.of(101.0, 1.0));
    private static final MultiLineString MULTI_LINE_STRING = MultiLineString.of(
            List.of(Position.of(100.0, 0.0), Position.of(101.0, 1.0)),
            List.of(Position.of(102.0, 2.0), Position.of(103.0, 3.0))
    );
    private static final MultiPolygon MULTI_POLYGON = MultiPolygon.of(
            PolygonCoordinates.of(List.of(Position.of(102.0, 2.0), Position.of(103.0, 2.0), Position.of(103.0, 3.0), Position.of(102.0, 3.0), Position.of(102.0, 2.0))),
            PolygonCoordinates.of(List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)), List.of(Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2)))
    );
    private static final String GEOMETRY_COLLECTION_WITH_ALL_TYPES = """
            { "type": "GeometryCollection","geometries": [
                { "type": "Point", "coordinates": [100.0, 0.0]},
                { "type": "LineString", "coordinates": [ [101.0, 0.0], [102.0, 1.0] ] },
                { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ] },
                { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8] ] ] },
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] },
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ] ] },
                { "type": "MultiPolygon", "coordinates": [ [ [ [102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0] ]], [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2] ] ] ] }
              ]
            }
            """;

    @Test
    void deserialize_withValidData_shouldCreateValidObject() throws IOException {
        GeometryCollection geometryCollection = objectMapper.readValue(GEOMETRY_COLLECTION_WITH_ALL_TYPES, GeometryCollection.class);

        assertThat(geometryCollection).isNotNull()
                .satisfies(geometry -> assertThat(geometry.getType()).isEqualTo(GEOMETRY_COLLECTION))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .extracting(GeometryCollection::getGeometries).asInstanceOf(InstanceOfAssertFactories.LIST).isNotEmpty()
                .hasSize(7)
                .contains(POINT, LINE_STRING, POLYGON, POLYGON_WITH_HOLES, MULTI_POINT, MULTI_LINE_STRING, MULTI_POLYGON);
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            { "type": "GeometryCollection","geometries": []}
            { "type": "GeometryCollection","geometries": null}
            """)
    void deserialize_withEmptyGeometry_shouldCreateValidObject(String json) throws IOException {
        GeometryCollection geometryCollection = objectMapper.readValue(json, GeometryCollection.class);

        assertThat(geometryCollection).isNotNull()
                .satisfies(geometry -> assertThat(geometry.getType()).isEqualTo(GEOMETRY_COLLECTION))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .satisfies(obj -> assertThat(obj.getGeometries()).isNullOrEmpty());
    }

    @Test
    void deserialize_withInvalidGeometry_shouldCreateObjectWithInvalidState() throws IOException {
        String json = """
                { "type": "GeometryCollection","geometries": [ { "type": "Point", "coordinates": [190.0, 100.0]} ] }""";
        GeometryCollection geometryCollection = objectMapper.readValue(json, GeometryCollection.class);

        assertThat(geometryCollection).isNotNull()
                .satisfies(geometry -> assertThat(geometry.getType()).isEqualTo(GEOMETRY_COLLECTION))
                .satisfies(obj -> assertThat(obj.isValid()).isFalse())
                .satisfies(obj -> assertThat(obj.getGeometries()).isNotNull());
    }

    @Test
    void serialize_withValidData_shouldCreateValidObject() throws IOException {
        GeometryCollection geometryCollection = GeometryCollection.of(POINT, LINE_STRING, POLYGON, POLYGON_WITH_HOLES, MULTI_POINT, MULTI_LINE_STRING, MULTI_POLYGON);
        String jsonContent = objectMapper.writeValueAsString(geometryCollection);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(GEOMETRY_COLLECTION_WITH_ALL_TYPES);
    }

    @Test
    void serialize_withValidData_withBaseTypeGeometry_shouldCreateValidObject() throws IOException {
        Geometry geometry = GeometryCollection.of(POINT, LINE_STRING, POLYGON, POLYGON_WITH_HOLES, MULTI_POINT, MULTI_LINE_STRING, MULTI_POLYGON);
        String jsonContent = objectMapper.writeValueAsString(geometry);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(GEOMETRY_COLLECTION_WITH_ALL_TYPES);
    }

    @Test
    void serialize_withValidData_withBaseTypeGeoJson_shouldCreateValidObject() throws IOException {
        GeoJson geoJson = GeometryCollection.of(POINT, LINE_STRING, POLYGON, POLYGON_WITH_HOLES, MULTI_POINT, MULTI_LINE_STRING, MULTI_POLYGON);
        String jsonContent = objectMapper.writeValueAsString(geoJson);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(GEOMETRY_COLLECTION_WITH_ALL_TYPES);
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        GeometryCollection geometryCollection = GeometryCollection.of(List.of(POINT, MULTI_POINT));
        assertThat(geometryCollection).hasToString("GeometryCollection{type='GeometryCollection', geometries=[Point{type='Point', coordinates=[100.0, 0.0]}, MultiPoint{type='MultiPoint', coordinates=[[100.0, 0.0], [101.0, 1.0]]}]}");
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        GeometryCollection location1Variant1 = GeometryCollection.of(POINT);
        GeometryCollection location1Variant2 = GeometryCollection.of(POINT);

        GeometryCollection location2Variant1 = GeometryCollection.of(MULTI_POINT);
        GeometryCollection location2Variant2 = GeometryCollection.of(MULTI_POINT);

        assertThat(location1Variant1).isEqualTo(location1Variant2);
        assertThat(location2Variant1).isEqualTo(location2Variant2);

        assertThat(location1Variant1).isNotEqualTo(location2Variant1);
        assertThat(location1Variant2).isNotEqualTo(location2Variant2);
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        GeometryCollection location1Variant1 = GeometryCollection.of(POINT);
        GeometryCollection location1Variant2 = GeometryCollection.of(POINT);

        GeometryCollection location2Variant1 = GeometryCollection.of(MULTI_POINT);
        GeometryCollection location2Variant2 = GeometryCollection.of(MULTI_POINT);

        assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
        assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
    }

}
