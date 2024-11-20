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

import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineStringTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization_withLongitudeAndLatitude_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "LineString", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] }""";
        LineString lineString = objectMapper.readValue(json, LineString.class);
        assertThat(lineString).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(LINE_STRING))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .extracting(LineString::getCoordinates).asInstanceOf(InstanceOfAssertFactories.LIST).isNotEmpty()
                .hasSize(2)
                .containsExactly(Position.of(100.0, 0.0), Position.of(101.0, 1.0));
    }

    @Test
    void deserialization_withLongitudeAndLatitudeAndAltitude_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "LineString", "coordinates": [ [100.0, 0.0, 45.0], [101.0, 1.0, 45.0] ] }""";
        LineString lineString = objectMapper.readValue(json, LineString.class);
        assertThat(lineString).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo("LineString"))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .extracting(LineString::getCoordinates).asInstanceOf(InstanceOfAssertFactories.LIST).isNotEmpty()
                .hasSize(2)
                .containsExactly(Position.of(100.0, 0.0, 45.0), Position.of(101.0, 1.0, 45.0));
    }

    @Test
    void deserialization_withLongitudeAndLatitudeAndAltitude_andWithManyCoordinates() throws IOException {
        String json = """
                { "type": "LineString", "coordinates": [ [100.0, 51.0, 45.0], [101.0, 52.0, 45.0], [102.0, 53.0, 45.0], [103.0, 54.0, 45.0], [104.0, 55.0, 45.0], [105.0, 56.0, 45.0], [106.0, 57.0, 45.0], [107.0, 58.0, 45.0], [108.0, 59.0, 45.0], [109.0, 60.0, 45.0], [110.0, 61.0, 45.0] ] }""";
        LineString lineString = objectMapper.readValue(json, LineString.class);
        assertThat(lineString).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(LINE_STRING))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .extracting(LineString::getCoordinates).asInstanceOf(InstanceOfAssertFactories.LIST).isNotEmpty()
                .hasSize(11)
                .containsExactly(
                        Position.of(100.0, 51.0, 45.0),
                        Position.of(101.0, 52.0, 45.0),
                        Position.of(102.0, 53.0, 45.0),
                        Position.of(103.0, 54.0, 45.0),
                        Position.of(104.0, 55.0, 45.0),
                        Position.of(105.0, 56.0, 45.0),
                        Position.of(106.0, 57.0, 45.0),
                        Position.of(107.0, 58.0, 45.0),
                        Position.of(108.0, 59.0, 45.0),
                        Position.of(109.0, 60.0, 45.0),
                        Position.of(110.0, 61.0, 45.0)
                );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
                    coordinates;        coordinates.longitude.invalid;      { "type": "LineString", "coordinates": [ [190.0, 0.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "LineString", "coordinates": [ [90.0, 91.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.invalid.min.length;     { "type": "LineString", "coordinates": [ [90.0, 45.0, 45.0] ] }
                    coordinates;        coordinates.invalid.empty;          { "type": "LineString", "coordinates": [] }
                    coordinates;        coordinates.longitude.invalid;      { "type": "LineString", "coordinates": [ [190.0, 90.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "LineString", "coordinates": [ [90.0, 90.0, 45.0], [101.0, -91.0, 45.0] ] }
            """
    )
    void deserialization_withInvalidData_shouldCreateObjectWithInvalidStatus(String expectedErrorField, String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, LineString.class)).isNotNull()
                .satisfies(lineString -> assertThat(lineString.getType()).isEqualTo("LineString"))
                .satisfies(lineString -> assertThat(lineString.getCoordinates()).isNotNull())
                .satisfies(lineString -> assertThat(lineString.isValid()).isFalse())
                .satisfies(lineString -> assertThat(lineString.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                .anySatisfy(error -> assertThat(error)
                                        .satisfies(e -> assertThat(e.getField()).isEqualTo(expectedErrorField))
                                        .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                )
                        )
                );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
                    coordinates;        coordinates.longitude.invalid;      { "type": "LineString", "coordinates": [ [190.0, 0.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "LineString", "coordinates": [ [90.0, 91.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.invalid.min.length;     { "type": "LineString", "coordinates": [ [90.0, 45.0, 45.0] ] }
                    coordinates;        coordinates.invalid.empty;          { "type": "LineString", "coordinates": [] }
                    coordinates;        coordinates.longitude.invalid;      { "type": "LineString", "coordinates": [ [190.0, 90.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "LineString", "coordinates": [ [90.0, 90.0, 45.0], [101.0, -91.0, 45.0] ] }
            """
    )
    void deserialization_withInvalidData_andWithBaseTypeGeometry_shouldCreateObjectWithInvalidStatus(String expectedErrorField, String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, Geometry.class)).isNotNull()
                .satisfies(lineString -> assertThat(lineString.getType()).isEqualTo("LineString"))
                .satisfies(lineString -> assertThat(lineString.isValid()).isFalse())
                .satisfies(lineString -> assertThat(lineString.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                .anySatisfy(error -> assertThat(error)
                                        .satisfies(e -> assertThat(e.getField()).isEqualTo(expectedErrorField))
                                        .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                )
                        )
                );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
                    coordinates;        coordinates.longitude.invalid;      { "type": "LineString", "coordinates": [ [190.0, 0.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "LineString", "coordinates": [ [90.0, 91.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.invalid.min.length;     { "type": "LineString", "coordinates": [ [90.0, 45.0, 45.0] ] }
                    coordinates;        coordinates.invalid.empty;          { "type": "LineString", "coordinates": [] }
                    coordinates;        coordinates.longitude.invalid;      { "type": "LineString", "coordinates": [ [190.0, 90.0, 45.0], [101.0, 1.0, 45.0] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "LineString", "coordinates": [ [90.0, 90.0, 45.0], [101.0, -91.0, 45.0] ] }
            """
    )
    void deserialization_withInvalidData_andWithBaseTypeGeoJson_shouldCreateObjectWithInvalidStatus(String expectedErrorField, String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, GeoJson.class)).isNotNull()
                .satisfies(lineString -> assertThat(lineString.getType()).isEqualTo("LineString"))
                .satisfies(lineString -> assertThat(lineString.isValid()).isFalse())
                .satisfies(lineString -> assertThat(lineString.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                .anySatisfy(error -> assertThat(error)
                                        .satisfies(e -> assertThat(e.getField()).isEqualTo(expectedErrorField))
                                        .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                )
                        )
                );
    }

    @Test
    void serialisation_withLongitudeAndLatitude_shouldSerializeAndProvideValidGeoJson() throws IOException {
        LineString lineString = LineString.of(Position.of(180, 90), Position.of(-180, -90));
        String jsonContent = objectMapper.writeValueAsString(lineString);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "LineString", "coordinates": [ [180.0, 90.0], [-180.0, -90.0] ] }""");
    }

    @Test
    void serialisation_withLongitudeLatitudeAndAltitude_shouldSerializeAndProvideValidGeoJson() throws IOException {
        LineString lineString = LineString.of(Position.of(180, 90, 45), Position.of(-180, -90, 45));
        String jsonContent = objectMapper.writeValueAsString(lineString);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "LineString", "coordinates": [ [180.0, 90.0, 45.0], [-180.0, -90.0, 45.0] ] }""");
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

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> LineString.of(positions));

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

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> LineString.of(positions));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        assertDoesNotThrow(() -> {
            LineString lineString = LineString.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));
            assertThat(lineString).hasToString("LineString{type='LineString', coordinates=[[100.012, 45.123456], [45.0, 65.0]]}");
        });
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        assertDoesNotThrow(() -> {
            LineString location1Variant1 = LineString.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));
            LineString location1Variant2 = LineString.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));

            LineString location2Variant1 = LineString.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));
            LineString location2Variant2 = LineString.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));

            assertThat(location1Variant1).isEqualTo(location1Variant2);
            assertThat(location2Variant1).isEqualTo(location2Variant2);

            assertThat(location1Variant1).isNotEqualTo(location2Variant1);
            assertThat(location1Variant2).isNotEqualTo(location2Variant2);
        });
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        assertDoesNotThrow(() -> {
            LineString location1Variant1 = LineString.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));
            LineString location1Variant2 = LineString.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0));

            LineString location2Variant1 = LineString.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));
            LineString location2Variant2 = LineString.of(Position.of(25.1234, -54.1234), Position.of(45.0, 65.0));

            assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
            assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
        });
    }


}
