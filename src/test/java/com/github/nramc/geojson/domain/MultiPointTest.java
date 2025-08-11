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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.geojson.constant.GeoJsonType;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultiPointTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialization_withLongitudeAndLatitude_shouldProvideValidJson() throws IOException {
        MultiPoint multiPoint = MultiPoint.of(
                Position.of(100, 50),
                Position.of(110, 60),
                Position.of(120, 70),
                Position.of(130, 80),
                Position.of(140, 85),
                Position.of(150, 90)
        );

        String jsonContent = objectMapper.writeValueAsString(multiPoint);

        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "MultiPoint", "coordinates": [ [100.0, 50.0], [110.0, 60.0], [120.0, 70.0], [130.0, 80.0], [140.0, 85.0], [150.0, 90.0] ] }""");
    }

    @Test
    void serialization_withLongitudeAndLatitudeAndAltitude_shouldProvideValidJson() throws IOException {
        MultiPoint multiPoint = MultiPoint.of(
                Position.of(100, 50, 5),
                Position.of(110, 60, 5),
                Position.of(120, 70, 5),
                Position.of(130, 80, 5),
                Position.of(140, 85, 5),
                Position.of(150, 90, 5)
        );

        String jsonContent = objectMapper.writeValueAsString(multiPoint);

        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "MultiPoint", "coordinates": [ [100.0, 50.0, 5.0], [110.0, 60.0, 5.0], [120.0, 70.0, 5.0], [130.0, 80.0, 5.0], [140.0, 85.0, 5.0], [150.0, 90.0, 5.0] ] }""");
    }

    @Test
    void deserialization_withValidLongitudeAndLatitude_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] }""";

        assertThat(objectMapper.readValue(json, MultiPoint.class)).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT))
                .satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2)
                        .containsExactly(Position.of(100, 0), Position.of(101, 1)));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitude_withGeometryBaseType_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] }""";

        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull().satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT));
        assertThat((MultiPoint) geometry)
                .satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2)
                        .containsExactly(Position.of(100, 0), Position.of(101, 1)));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitude_withGeoJsonBaseType_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] }""";

        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull().satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT));
        assertThat((MultiPoint) geoJson)
                .satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2)
                        .containsExactly(Position.of(100, 0), Position.of(101, 1)));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitudeAndAltitude_shouldProvideValidObject() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] }""";

        assertThat(objectMapper.readValue(json, MultiPoint.class)).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT))
                .satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2)
                        .containsExactly(Position.of(100, 0, 5), Position.of(101, 1, 5)));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitudeAndAltitude_withGeometryBaseType_shouldProvideValidObject() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] }""";

        Geometry geometry = objectMapper.readValue(json, Geometry.class);
        assertThat(geometry).isNotNull().satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT));

        assertThat((MultiPoint) geometry).satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2)
                .containsExactly(Position.of(100, 0, 5), Position.of(101, 1, 5)));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitudeAndAltitude_withGeoJsonBaseType_shouldProvideValidObject() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] }""";

        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull().satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT));

        assertThat((MultiPoint) geoJson).satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2)
                .containsExactly(Position.of(100, 0, 5), Position.of(101, 1, 5)));
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # GeoJson,                                                                              Expected Error
            { "type": "MultiPoint", "coordinates": [ ] };                                           coordinates.invalid.empty
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0, 1.0, 5.0, 4.0] ] }; coordinates.length.invalid
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0] ] };                coordinates.length.invalid
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] };      coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 180.0, 5.0], [101.0, 1.0, 5.0] ] };    coordinates.latitude.invalid
            { "type": "MultiPoint", "coordinates": [ [-200.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] };     coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0], [101.0, -100.0, 5.0] ] };   coordinates.latitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0] ] };                         coordinates.invalid.min.length
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [201.0, 1.0, 5.0] ] };      coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 100.0, 5.0], [101.0, 100.0, 5.0] ] };  coordinates.latitude.invalid
            """)
    void deserialization_withInvalidJson_shouldCreateObject_withInvalidStatus(String geoJson, String expectedErrorKey) throws JsonProcessingException {
        assertThat(objectMapper.readValue(geoJson, MultiPoint.class))
                .satisfies(point -> assertThat(point.getType()).isEqualTo("MultiPoint"))
                .satisfies(point -> assertThat(point.getCoordinates()).isNotNull())
                .satisfies(point -> assertThat(point.isValid()).isFalse())
                .satisfies(point -> assertThat(point.validate())
                                .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                                .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                                .anySatisfy(error -> assertThat(error)
                                                                .satisfies(e -> assertThat(e.getField()).isEqualTo("coordinates"))
                                                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                                )
                                )
                );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # GeoJson,                                                                              Expected Error
            { "type": "MultiPoint", "coordinates": [ ] };                                           coordinates.invalid.empty
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0, 1.0, 5.0, 4.0] ] }; coordinates.length.invalid
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0] ] };                coordinates.length.invalid
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] };      coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 180.0, 5.0], [101.0, 1.0, 5.0] ] };    coordinates.latitude.invalid
            { "type": "MultiPoint", "coordinates": [ [-200.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] };     coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0], [101.0, -100.0, 5.0] ] };   coordinates.latitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0] ] };                         coordinates.invalid.min.length
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [201.0, 1.0, 5.0] ] };      coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 100.0, 5.0], [101.0, 100.0, 5.0] ] };  coordinates.latitude.invalid
            """)
    void deserialization_withInvalidJson_withGeometryBaseType_shouldCreateObject_withInvalidStatus(String geoJson, String expectedErrorKey) throws JsonProcessingException {
        assertThat(objectMapper.readValue(geoJson, Geometry.class))
                .satisfies(point -> assertThat(point.getType()).isEqualTo("MultiPoint"))
                .satisfies(point -> assertThat(point.isValid()).isFalse())
                .satisfies(point -> assertThat(point.validate())
                                .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                                .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                                .anySatisfy(error -> assertThat(error)
                                                                .satisfies(e -> assertThat(e.getField()).isEqualTo("coordinates"))
                                                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                                )
                                )
                );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # GeoJson,                                                                              Expected Error
            { "type": "MultiPoint", "coordinates": [ ] };                                           coordinates.invalid.empty
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0, 1.0, 5.0, 4.0] ] }; coordinates.length.invalid
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0] ] };                coordinates.length.invalid
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] };      coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 180.0, 5.0], [101.0, 1.0, 5.0] ] };    coordinates.latitude.invalid
            { "type": "MultiPoint", "coordinates": [ [-200.0, 0.0, 5.0], [101.0, 1.0, 5.0] ] };     coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0], [101.0, -100.0, 5.0] ] };   coordinates.latitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 0.0, 5.0] ] };                         coordinates.invalid.min.length
            { "type": "MultiPoint", "coordinates": [ [200.0, 0.0, 5.0], [201.0, 1.0, 5.0] ] };      coordinates.longitude.invalid
            { "type": "MultiPoint", "coordinates": [ [100.0, 100.0, 5.0], [101.0, 100.0, 5.0] ] };  coordinates.latitude.invalid
            """)
    void deserialization_withInvalidJson_withGeoJsonBaseType_shouldCreateObject_withInvalidStatus(String geoJson, String expectedErrorKey) throws JsonProcessingException {
        assertThat(objectMapper.readValue(geoJson, GeoJson.class))
                .satisfies(point -> assertThat(point.getType()).isEqualTo("MultiPoint"))
                .satisfies(point -> assertThat(point.isValid()).isFalse())
                .satisfies(point -> assertThat(point.validate())
                                .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                                .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                                .anySatisfy(error -> assertThat(error)
                                                                .satisfies(e -> assertThat(e.getField()).isEqualTo("coordinates"))
                                                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                                )
                                )
                );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsList() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.invalid.empty", List.of()),
                Arguments.of("coordinates", "coordinates.invalid.min.length", List.of(Position.of(100.012, 45.123456))),
                Arguments.of("coordinates", "coordinates.longitude.invalid", List.of(Position.of(100.012, 45.123456), new Position(new double[]{-190, 45}))),
                Arguments.of("coordinates", "coordinates.latitude.invalid", List.of(Position.of(100.012, 45.123456), new Position(new double[]{-180, 145})))
        );
    }

    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsList")
    void eagerValidation_withList_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<Position> positions) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> MultiPoint.of(positions));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsVararg() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.invalid.empty", null),
                Arguments.of("coordinates", "coordinates.invalid.min.length", new Position[]{Position.of(100.012, 45.123456)}),
                Arguments.of("coordinates", "coordinates.longitude.invalid", new Position[]{Position.of(100.012, 45.123456), new Position(new double[]{-190, 45})}),
                Arguments.of("coordinates", "coordinates.latitude.invalid", new Position[]{Position.of(100.012, 45.123456), new Position(new double[]{-180, 145})})
        );
    }

    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsVararg")
    void eagerValidation_withVarArg_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, Position... positions) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> MultiPoint.of(positions));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        MultiPoint multiPoint = MultiPoint.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));
        assertThat(multiPoint).hasToString("MultiPoint{type='MultiPoint', coordinates=[[100.012, 45.123456], [45.0, 65.0]]}");
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        MultiPoint location1Variant1 = MultiPoint.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));
        MultiPoint location1Variant2 = MultiPoint.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));

        MultiPoint location2Variant1 = MultiPoint.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));
        MultiPoint location2Variant2 = MultiPoint.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));

        assertThat(location1Variant1).isEqualTo(location1Variant2);
        assertThat(location2Variant1).isEqualTo(location2Variant2);

        assertThat(location1Variant1).isNotEqualTo(location2Variant1);
        assertThat(location1Variant2).isNotEqualTo(location2Variant2);
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        MultiPoint location1Variant1 = MultiPoint.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));
        MultiPoint location1Variant2 = MultiPoint.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));

        MultiPoint location2Variant1 = MultiPoint.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));
        MultiPoint location2Variant2 = MultiPoint.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));

        assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
        assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
    }

}
