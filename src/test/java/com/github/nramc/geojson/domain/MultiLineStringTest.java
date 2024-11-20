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

import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultiLineStringTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization_withValidLongitudeAndLatitude_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }""";
        assertThat(objectMapper.readValue(json, MultiLineString.class))
                .isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_LINE_STRING))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5)
                        .containsExactly(
                                List.of(Position.of(100, 0), Position.of(101, 1)),
                                List.of(Position.of(102, 2), Position.of(103, 3)),
                                List.of(Position.of(104, 4), Position.of(105, 5)),
                                List.of(Position.of(106, 6), Position.of(107, 7)),
                                List.of(Position.of(108, 8), Position.of(109, 9))
                        )
                );
    }

    @Test
    void deserialization_withValidLongitudeAndLatitudeAndAltitude_shouldCreateValidObject() throws IOException {
        String json = """
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0, 0], [101.0, 1.0, 1] ], [ [102.0, 2.0, 2], [103.0, 3.0, 3] ], [ [104.0, 4.0, 4], [105.0, 5.0, 5] ], [ [106.0, 6.0, 6], [107.0, 7.0, 7] ], [ [108.0, 8.0, 8], [109.0, 9.0, 9] ] ] }""";
        assertThat(objectMapper.readValue(json, MultiLineString.class))
                .isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_LINE_STRING))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .satisfies(obj -> assertThat(obj.getCoordinates()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5)
                        .containsExactly(
                                List.of(Position.of(100, 0, 0), Position.of(101, 1, 1)),
                                List.of(Position.of(102, 2, 2), Position.of(103, 3, 3)),
                                List.of(Position.of(104, 4, 4), Position.of(105, 5, 5)),
                                List.of(Position.of(106, 6, 6), Position.of(107, 7, 7)),
                                List.of(Position.of(108, 8, 8), Position.of(109, 9, 9))
                        )
                );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
                    coordinates;        coordinates.longitude.invalid;      { "type": "MultiLineString", "coordinates": [ [ [190.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "MultiLineString", "coordinates": [ [ [100.0, 180.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }
                    coordinates;        coordinates.invalid.min.length;     { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0] ] ] }
                    coordinates;        coordinates.invalid.empty;          { "type": "MultiLineString", "coordinates": [] }
                    coordinates;        coordinates.longitude.invalid;      { "type": "MultiLineString", "coordinates": [ [ [-190.0, 0.0, 11000], [101.0, 1.0, 1000] ], [ [102.0, 2.0, 1000], [103.0, 3.0, 1000] ], [ [104.0, 4.0, 2000], [105.0, 5.0, 2000] ], [ [106.0, 6.0,3000], [107.0, 7.0, 3000] ], [ [108.0, 8.0, 4000], [109.0, 9.0, 4000] ] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "MultiLineString", "coordinates": [ [ [100.0, -95.0, 1000], [101.0, 1.0, 1000] ], [ [102.0, 2.0, 1000], [103.0, 3.0, 1000] ], [ [104.0, 4.0, 1000], [105.0, 5.0, 1000] ], [ [106.0, 6.0, 1000], [107.0, 7.0, 1000] ], [ [108.0, 8.0, 1000], [109.0, 9.0, 1000] ] ] }
            """
    )
    void deserialization_withInvalidData_shouldCreateObjectWithInvalidStatus(String expectedErrorField, String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, MultiLineString.class)).isNotNull()
                .satisfies(multiLineString -> assertThat(multiLineString.getType()).isEqualTo("MultiLineString"))
                .satisfies(multiLineString -> assertThat(multiLineString.getCoordinates()).isNotNull())
                .satisfies(multiLineString -> assertThat(multiLineString.isValid()).isFalse())
                .satisfies(multiLineString -> assertThat(multiLineString.validate())
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
                    coordinates;        coordinates.longitude.invalid;      { "type": "MultiLineString", "coordinates": [ [ [190.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "MultiLineString", "coordinates": [ [ [100.0, 180.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }
                    coordinates;        coordinates.invalid.min.length;     { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0] ] ] }
                    coordinates;        coordinates.invalid.empty;          { "type": "MultiLineString", "coordinates": [] }
                    coordinates;        coordinates.longitude.invalid;      { "type": "MultiLineString", "coordinates": [ [ [-190.0, 0.0, 11000], [101.0, 1.0, 1000] ], [ [102.0, 2.0, 1000], [103.0, 3.0, 1000] ], [ [104.0, 4.0, 2000], [105.0, 5.0, 2000] ], [ [106.0, 6.0,3000], [107.0, 7.0, 3000] ], [ [108.0, 8.0, 4000], [109.0, 9.0, 4000] ] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "MultiLineString", "coordinates": [ [ [100.0, -95.0, 1000], [101.0, 1.0, 1000] ], [ [102.0, 2.0, 1000], [103.0, 3.0, 1000] ], [ [104.0, 4.0, 1000], [105.0, 5.0, 1000] ], [ [106.0, 6.0, 1000], [107.0, 7.0, 1000] ], [ [108.0, 8.0, 1000], [109.0, 9.0, 1000] ] ] }
            """
    )
    void deserialization_withInvalidData_andWithBaseTypeGeometry_shouldCreateObjectWithInvalidStatus(String expectedErrorField, String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, Geometry.class)).isNotNull()
                .satisfies(multiLineString -> assertThat(multiLineString.getType()).isEqualTo("MultiLineString"))
                .satisfies(multiLineString -> assertThat(multiLineString.isValid()).isFalse())
                .satisfies(multiLineString -> assertThat(multiLineString.validate())
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
                    coordinates;        coordinates.longitude.invalid;      { "type": "MultiLineString", "coordinates": [ [ [190.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "MultiLineString", "coordinates": [ [ [100.0, 180.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }
                    coordinates;        coordinates.invalid.min.length;     { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0] ] ] }
                    coordinates;        coordinates.invalid.empty;          { "type": "MultiLineString", "coordinates": [] }
                    coordinates;        coordinates.longitude.invalid;      { "type": "MultiLineString", "coordinates": [ [ [-190.0, 0.0, 11000], [101.0, 1.0, 1000] ], [ [102.0, 2.0, 1000], [103.0, 3.0, 1000] ], [ [104.0, 4.0, 2000], [105.0, 5.0, 2000] ], [ [106.0, 6.0,3000], [107.0, 7.0, 3000] ], [ [108.0, 8.0, 4000], [109.0, 9.0, 4000] ] ] }
                    coordinates;        coordinates.latitude.invalid;       { "type": "MultiLineString", "coordinates": [ [ [100.0, -95.0, 1000], [101.0, 1.0, 1000] ], [ [102.0, 2.0, 1000], [103.0, 3.0, 1000] ], [ [104.0, 4.0, 1000], [105.0, 5.0, 1000] ], [ [106.0, 6.0, 1000], [107.0, 7.0, 1000] ], [ [108.0, 8.0, 1000], [109.0, 9.0, 1000] ] ] }
            """
    )
    void deserialization_withInvalidData_withBaseTypeGeoJson_shouldCreateObjectWithInvalidStatus(String expectedErrorField, String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, GeoJson.class)).isNotNull()
                .satisfies(multiLineString -> assertThat(multiLineString.getType()).isEqualTo("MultiLineString"))
                .satisfies(multiLineString -> assertThat(multiLineString.isValid()).isFalse())
                .satisfies(multiLineString -> assertThat(multiLineString.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isTrue())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors())
                                .anySatisfy(error -> assertThat(error)
                                        .satisfies(e -> assertThat(e.getField()).isEqualTo(expectedErrorField))
                                        .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                                )
                        )
                );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsList() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.invalid.empty", List.of()),
                Arguments.of("coordinates", "coordinates.invalid.min.length", List.of(List.of(Position.of(100.0, 0.0)))),
                Arguments.of("coordinates", "coordinates.longitude.invalid", List.of(
                        List.of(new Position(new double[]{-190.0, 0.0, 11000}), Position.of(101.0, 1.0, 1000)),
                        List.of(Position.of(102.0, 2.0, 1000), Position.of(103.0, 3.0, 1000)),
                        List.of(Position.of(104.0, 4.0, 2000), Position.of(105.0, 5.0, 2000)),
                        List.of(Position.of(106.0, 6.0, 3000), Position.of(107.0, 7.0, 3000)),
                        List.of(Position.of(108.0, 8.0, 4000), Position.of(109.0, 9.0, 4000))
                )),
                Arguments.of("coordinates", "coordinates.latitude.invalid", List.of(
                        List.of(new Position(new double[]{100.0, -95.0, 1000}), Position.of(101.0, 1.0, 1000)),
                        List.of(Position.of(102.0, 2.0, 1000), Position.of(103.0, 3.0, 1000)),
                        List.of(Position.of(104.0, 4.0, 1000), Position.of(105.0, 5.0, 1000)),
                        List.of(Position.of(106.0, 6.0, 1000), Position.of(107.0, 7.0, 1000)),
                        List.of(Position.of(108.0, 8.0, 1000), Position.of(109.0, 9.0, 1000))
                )),
                Arguments.of("coordinates", "coordinates.longitude.invalid", List.of(
                        List.of(new Position(new double[]{190.0, 0.0}), Position.of(101.0, 1.0)),
                        List.of(Position.of(102.0, 2.0), Position.of(103.0, 3.0)),
                        List.of(Position.of(104.0, 4.0), Position.of(105.0, 5.0)),
                        List.of(Position.of(106.0, 6.0), Position.of(107.0, 7.0)),
                        List.of(Position.of(108.0, 8.0), Position.of(109.0, 9.0))
                )),
                Arguments.of("coordinates", "coordinates.latitude.invalid", List.of(
                        List.of(new Position(new double[]{100.0, 180.0}), Position.of(101.0, 1.0)),
                        List.of(Position.of(102.0, 2.0), Position.of(103.0, 3.0)),
                        List.of(Position.of(104.0, 4.0), Position.of(105.0, 5.01050, 5.0)),
                        List.of(Position.of(106.0, 6.0), Position.of(107.0, 7.0)),
                        List.of(Position.of(108.0, 8.0), Position.of(109.0, 9.0))
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsList")
    final void eagerValidation_withList_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<List<Position>> coordinates) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> MultiLineString.of(coordinates));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsVararg() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.invalid.empty", new List[]{}),
                Arguments.of("coordinates", "coordinates.invalid.min.length", new List[]{List.of(Position.of(100.0, 0.0))}),
                Arguments.of("coordinates", "coordinates.longitude.invalid", new List[]{
                        List.of(new Position(new double[]{-190.0, 0.0, 11000}), Position.of(101.0, 1.0, 1000)),
                        List.of(Position.of(102.0, 2.0, 1000), Position.of(103.0, 3.0, 1000)),
                        List.of(Position.of(104.0, 4.0, 2000), Position.of(105.0, 5.0, 2000)),
                        List.of(Position.of(106.0, 6.0, 3000), Position.of(107.0, 7.0, 3000)),
                        List.of(Position.of(108.0, 8.0, 4000), Position.of(109.0, 9.0, 4000))
                }),
                Arguments.of("coordinates", "coordinates.latitude.invalid", new List[]{
                        List.of(new Position(new double[]{100.0, -95.0, 1000}), Position.of(101.0, 1.0, 1000)),
                        List.of(Position.of(102.0, 2.0, 1000), Position.of(103.0, 3.0, 1000)),
                        List.of(Position.of(104.0, 4.0, 1000), Position.of(105.0, 5.0, 1000)),
                        List.of(Position.of(106.0, 6.0, 1000), Position.of(107.0, 7.0, 1000)),
                        List.of(Position.of(108.0, 8.0, 1000), Position.of(109.0, 9.0, 1000))
                }),
                Arguments.of("coordinates", "coordinates.longitude.invalid", new List[]{
                        List.of(new Position(new double[]{190.0, 0.0}), Position.of(101.0, 1.0)),
                        List.of(Position.of(102.0, 2.0), Position.of(103.0, 3.0)),
                        List.of(Position.of(104.0, 4.0), Position.of(105.0, 5.0)),
                        List.of(Position.of(106.0, 6.0), Position.of(107.0, 7.0)),
                        List.of(Position.of(108.0, 8.0), Position.of(109.0, 9.0))
                }),
                Arguments.of("coordinates", "coordinates.latitude.invalid", new List[]{
                        List.of(new Position(new double[]{100.0, 180.0}), Position.of(101.0, 1.0)),
                        List.of(Position.of(102.0, 2.0), Position.of(103.0, 3.0)),
                        List.of(Position.of(104.0, 4.0), Position.of(105.0, 5.01050, 5.0)),
                        List.of(Position.of(106.0, 6.0), Position.of(107.0, 7.0)),
                        List.of(Position.of(108.0, 8.0), Position.of(109.0, 9.0))
                })
        );
    }

    @SafeVarargs
    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsVararg")
    final void eagerValidation_withVarArg_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<Position>... lines) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> MultiLineString.of(lines));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    @Test
    void serialization_withValidLongitudeAndLatitudeAndAltitude_shouldCreateValidGeoJson() throws IOException {
        MultiLineString multiLineString = MultiLineString.of(
                List.of(Position.of(100, 0, 0), Position.of(101, 1, 1)),
                List.of(Position.of(102, 2, 2), Position.of(103, 3, 3)),
                List.of(Position.of(104, 4, 4), Position.of(105, 5, 5)),
                List.of(Position.of(106, 6, 6), Position.of(107, 7, 7)),
                List.of(Position.of(108, 8, 8), Position.of(109, 9, 9))
        );

        String jsonContent = objectMapper.writeValueAsString(multiLineString);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0, 0.0], [101.0, 1.0, 1.0] ], [ [102.0, 2.0, 2.0], [103.0, 3.0, 3.0] ], [ [104.0, 4.0, 4.0], [105.0, 5.0, 5.0] ], [ [106.0, 6.0, 6.0], [107.0, 7.0, 7.0] ], [ [108.0, 8.0, 8.0], [109.0, 9.0, 9.0] ] ] }""");
    }

    @Test
    void serialization_withValidLongitudeAndLatitude_shouldCreateValidGeoJso() throws IOException {
        MultiLineString multiLineString = MultiLineString.of(
                List.of(Position.of(100, 0), Position.of(101, 1)),
                List.of(Position.of(102, 2), Position.of(103, 3)),
                List.of(Position.of(104, 4), Position.of(105, 5)),
                List.of(Position.of(106, 6), Position.of(107, 7)),
                List.of(Position.of(108, 8), Position.of(109, 9))
        );

        String jsonContent = objectMapper.writeValueAsString(multiLineString);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ], [ [104.0, 4.0], [105.0, 5.0] ], [ [106.0, 6.0], [107.0, 7.0] ], [ [108.0, 8.0], [109.0, 9.0] ] ] }""");
    }


    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        assertDoesNotThrow(() -> {
            MultiLineString multiLineString = MultiLineString.of(List.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0)));
            assertThat(multiLineString).hasToString("MultiLineString{type='MultiLineString', coordinates=[[[100.012, 45.123456], [45.0, 65.0]]]}");
        });
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        assertDoesNotThrow(() -> {
            MultiLineString location1Variant1 = MultiLineString.of(List.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0)));
            MultiLineString location1Variant2 = MultiLineString.of(List.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0)));

            MultiLineString location2Variant1 = MultiLineString.of(List.of(Position.of(-100.012, -45.123456), Position.of(45.0, 65.0)));
            MultiLineString location2Variant2 = MultiLineString.of(List.of(Position.of(-100.012, -45.123456), Position.of(45.0, 65.0)));

            assertThat(location1Variant1).isEqualTo(location1Variant2);
            assertThat(location2Variant1).isEqualTo(location2Variant2);

            assertThat(location1Variant1).isNotEqualTo(location2Variant1);
            assertThat(location1Variant2).isNotEqualTo(location2Variant2);
        });
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        assertDoesNotThrow(() -> {
            MultiLineString location1Variant1 = MultiLineString.of(List.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0)));
            MultiLineString location1Variant2 = MultiLineString.of(List.of(Position.of(100.012, 45.123456), Position.of(45.0, 65.0)));

            MultiLineString location2Variant1 = MultiLineString.of(List.of(Position.of(-100.012, -45.123456), Position.of(45.0, 65.0)));
            MultiLineString location2Variant2 = MultiLineString.of(List.of(Position.of(-100.012, -45.123456), Position.of(45.0, 65.0)));

            assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
            assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
        });
    }


}
