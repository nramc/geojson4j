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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PolygonCoordinatesTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization_withValidExterior_andEmptyHoles_shouldCreateValidObject() throws IOException {
        String jsonContent = """
                [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]""";
        assertThat(objectMapper.readValue(jsonContent, PolygonCoordinates.class)).isNotNull()
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .satisfies(obj -> assertThat(obj.getExterior()).isNotNull().asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5)
                        .containsExactly(
                                Position.of(100, 0),
                                Position.of(101, 0),
                                Position.of(101, 1),
                                Position.of(100, 1),
                                Position.of(100, 0)
                        ))
                .satisfies(obj -> assertThat(obj.getHoles()).isNullOrEmpty());

    }

    @Test
    void deserialization_withValidExterior_andWithValidHoles_shouldCreateValidObject() throws IOException {
        String jsonContent = """
                [
                 [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],
                 [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8] ]
                ]""";
        assertThat(objectMapper.readValue(jsonContent, PolygonCoordinates.class)).isNotNull()
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .satisfies(obj -> assertThat(obj.getExterior()).isNotNull().asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5)
                        .containsExactly(
                                Position.of(100, 0),
                                Position.of(101, 0),
                                Position.of(101, 1),
                                Position.of(100, 1),
                                Position.of(100, 0)
                        ))
                .satisfies(obj -> assertThat(obj.getHoles()).isNotEmpty().asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1)
                        .containsExactly(List.of(
                                Position.of(100.8, 0.8),
                                Position.of(100.8, 0.2),
                                Position.of(100.2, 0.2),
                                Position.of(100.2, 0.8),
                                Position.of(100.8, 0.8)
                        )));
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            #Error Key                          GeoJson
            coordinates.exterior.ring.empty;    [ [ ] ]
            coordinates.ring.length.invalid;    [ [ [100.0, 0.0], [101.0, 0.0], [100.0, 0.0] ] ]
            coordinates.ring.circle.invalid;    [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [180.0, 0.0] ] ]
            coordinates.ring.length.invalid;    [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.8, 0.8] ] ]
            coordinates.ring.circle.invalid;    [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [180.8, 0.8] ] ]
            coordinates.longitude.invalid;    [ [ [100.0, 0.0], [200.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8] ] ]
            coordinates.latitude.invalid;    [ [ [100.0, 0.0], [101.0, 200.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8] ] ]
            """)
    void deserialization_withInvalidJson_shouldCreateObjectWithInvalidState(String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, PolygonCoordinates.class)).isNotNull()
                .satisfies(obj -> assertThat(obj.isValid()).isFalse())
                .satisfies(obj -> assertThat(obj.getCoordinates()).isNotNull())
                .satisfies(obj -> assertThat(obj.validate()).isNotNull()
                        .satisfies(validationResult -> assertThat(validationResult.getErrors()).isNotEmpty()
                                .anySatisfy(error -> assertThat(error.getKey()).isEqualTo(expectedErrorKey))
                        )
                );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsList() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.exterior.ring.empty", List.of()),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", List.of(
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0))
                )),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", List.of(
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(180.0, 0.0))
                )),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", List.of(
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
                        List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.8, 0.8))
                )),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", List.of(
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
                        List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(120.8, 0.8))
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsList")
    final void eagerValidation_withList_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<List<Position>> coordinates) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> PolygonCoordinates.of(coordinates));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsVararg() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.exterior.ring.empty", new List[]{}),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", new List[]{
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0))
                }),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", new List[]{
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(180.0, 0.0))
                }),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", new List[]{
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
                        List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.8, 0.8))
                }),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", new List[]{
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
                        List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(120.8, 0.8))
                })
        );
    }

    @SafeVarargs
    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsVararg")
    final void eagerValidation_withVarArg_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<Position>... lines) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> PolygonCoordinates.of(lines));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    @Test
    void serialisation_withExterior_andWithoutHoles_shouldCreateValidGeoJson() throws IOException {
        List<Position> exteriorRing = List.of(
                Position.of(100, 0),
                Position.of(101, 0),
                Position.of(101, 1),
                Position.of(100, 1),
                Position.of(100, 0)
        );
        PolygonCoordinates coordinates = PolygonCoordinates.of(exteriorRing);
        String jsonContent = objectMapper.writeValueAsString(coordinates);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]""");
    }

    @Test
    void serialization_withHoles_shouldCreateValidJson() throws IOException {
        List<Position> exteriorRing = List.of(
                Position.of(100, 0),
                Position.of(101, 0),
                Position.of(101, 1),
                Position.of(100, 1),
                Position.of(100, 0)
        );
        List<Position> hole = List.of(
                Position.of(100.8, 0.8),
                Position.of(100.8, 0.2),
                Position.of(100.2, 0.2),
                Position.of(100.2, 0.8),
                Position.of(100.8, 0.8)
        );

        PolygonCoordinates polygonCoordinates = PolygonCoordinates.of(exteriorRing, hole);
        String jsonContent = objectMapper.writeValueAsString(polygonCoordinates);

        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                [
                    [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],
                    [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8] ]
                ]""");
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        assertDoesNotThrow(() -> {
            List<Position> exteriorRing = List.of(
                    Position.of(100, 0),
                    Position.of(101, 0),
                    Position.of(101, 1),
                    Position.of(100, 1),
                    Position.of(100, 0)
            );
            List<Position> hole = List.of(
                    Position.of(100.8, 0.8),
                    Position.of(100.8, 0.2),
                    Position.of(100.2, 0.2),
                    Position.of(100.2, 0.8),
                    Position.of(100.8, 0.8)
            );

            PolygonCoordinates polygonCoordinates = PolygonCoordinates.of(exteriorRing, hole);
            assertThat(polygonCoordinates).hasToString("[[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]]");
        });
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        assertDoesNotThrow(() -> {
            List<Position> exteriorRing = List.of(
                    Position.of(100, 0),
                    Position.of(101, 0),
                    Position.of(101, 1),
                    Position.of(100, 1),
                    Position.of(100, 0)
            );
            List<Position> hole = List.of(
                    Position.of(100.8, 0.8),
                    Position.of(100.8, 0.2),
                    Position.of(100.2, 0.2),
                    Position.of(100.2, 0.8),
                    Position.of(100.8, 0.8)
            );

            PolygonCoordinates location1Variant1 = PolygonCoordinates.of(exteriorRing, hole);
            PolygonCoordinates location1Variant2 = PolygonCoordinates.of(exteriorRing, hole);

            PolygonCoordinates location2Variant1 = PolygonCoordinates.of(hole, exteriorRing);
            PolygonCoordinates location2Variant2 = PolygonCoordinates.of(hole, exteriorRing);

            assertThat(location1Variant1).isEqualTo(location1Variant2);
            assertThat(location2Variant1).isEqualTo(location2Variant2);

            assertThat(location1Variant1).isNotEqualTo(location2Variant1);
            assertThat(location1Variant2).isNotEqualTo(location2Variant2);
        });
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        assertDoesNotThrow(() -> {
            List<Position> exteriorRing = List.of(
                    Position.of(100, 0),
                    Position.of(101, 0),
                    Position.of(101, 1),
                    Position.of(100, 1),
                    Position.of(100, 0)
            );
            List<Position> hole = List.of(
                    Position.of(100.8, 0.8),
                    Position.of(100.8, 0.2),
                    Position.of(100.2, 0.2),
                    Position.of(100.2, 0.8),
                    Position.of(100.8, 0.8)
            );

            PolygonCoordinates location1Variant1 = PolygonCoordinates.of(exteriorRing, hole);
            PolygonCoordinates location1Variant2 = PolygonCoordinates.of(exteriorRing, hole);

            PolygonCoordinates location2Variant1 = PolygonCoordinates.of(hole, exteriorRing);
            PolygonCoordinates location2Variant2 = PolygonCoordinates.of(hole, exteriorRing);

            assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
            assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
        });
    }

}
