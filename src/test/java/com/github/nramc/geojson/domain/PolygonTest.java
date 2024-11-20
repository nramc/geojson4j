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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PolygonTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String POLYGON_WITHOUT_HOLES_JSON = """
            { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ] }""";
    private static final String POLYGON_WITH_HOLES_JSON = """
            { "type": "Polygon", "coordinates": [
                [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],
                [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8] ]
            ] }""";
    private static final List<Position> EXTERIOR_RING = List.of(
            Position.of(100, 0), Position.of(101, 0), Position.of(101, 1), Position.of(100, 1), Position.of(100, 0)
    );
    private static final List<Position> INTERIOR_RING = List.of(
            Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(100.8, 0.8)
    );

    @Test
    void deserialization_withValidExterior_shouldCreateValidObject() throws IOException {
        Polygon obj = objectMapper.readValue(POLYGON_WITHOUT_HOLES_JSON, Polygon.class);

        assertThat(obj).isNotNull()
                .satisfies(polygon -> assertThat(polygon.getType()).isEqualTo(POLYGON))
                .satisfies(polygon -> assertThat(polygon.isValid()).isTrue())
                .extracting(Polygon::getCoordinates).isNotNull()
                .satisfies(coordinates -> assertThat(coordinates.getCoordinates()).isNotEmpty().containsExactly(EXTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.getExterior()).isNotEmpty().containsAll(EXTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.getHoles()).isNullOrEmpty());
    }

    @Test
    void deserialization_withValidExteriorAndHoles_shouldCreateValidObject() throws IOException {
        Polygon obj = objectMapper.readValue(POLYGON_WITH_HOLES_JSON, Polygon.class);

        assertThat(obj).isNotNull()
                .satisfies(polygon -> assertThat(polygon.getType()).isEqualTo(POLYGON))
                .satisfies(polygon -> assertThat(polygon.isValid()).isTrue())
                .extracting(Polygon::getCoordinates).isNotNull()
                .satisfies(coordinates -> assertThat(coordinates.getCoordinates()).isNotEmpty().containsExactly(EXTERIOR_RING, INTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.getExterior()).isNotEmpty().containsAll(EXTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.getHoles()).isNotEmpty().containsExactly(INTERIOR_RING));
    }

    @Test
    void deserialization_withValidExteriorAndHoles_withBaseTypeGeometry_shouldCreateValidObject() throws IOException {
        Geometry obj = objectMapper.readValue(POLYGON_WITH_HOLES_JSON, Geometry.class);

        assertThat(obj).isNotNull()
                .satisfies(polygon -> assertThat(polygon.getType()).isEqualTo(POLYGON))
                .satisfies(polygon -> assertThat(polygon.isValid()).isTrue());
    }

    @Test
    void deserialization_withValidExteriorAndHoles_withBaseTypeGeoJson_shouldCreateValidObject() throws IOException {
        GeoJson obj = objectMapper.readValue(POLYGON_WITH_HOLES_JSON, GeoJson.class);

        assertThat(obj).isNotNull()
                .satisfies(polygon -> assertThat(polygon.getType()).isEqualTo(POLYGON))
                .satisfies(polygon -> assertThat(polygon.isValid()).isTrue());
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # Error Key                         GeoJson
            coordinates.exterior.ring.empty;    { "type": "Polygon", "coordinates": [ [ ] ] }
            coordinates.ring.length.invalid;    { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [100.0, 0.0] ] ] }
            coordinates.ring.circle.invalid;    { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [180.0, 0.0] ] ] }
            coordinates.ring.length.invalid;    { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.8, 0.8] ] ] }
            coordinates.ring.circle.invalid;    { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [180.8, 0.8] ] ] }
            coordinates.longitude.invalid;      { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [190.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ] }
            coordinates.latitude.invalid;       { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 95.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ] }
            """)
    void deserialization_withInvalidJson_shouldCreateObjectWithInvalidState(String expectedErrorKey, String json) throws JsonProcessingException {
        assertThat(objectMapper.readValue(json, Polygon.class)).isNotNull()
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

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Polygon.of(coordinates));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsVararg() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.exterior.ring.empty", List.of(), new List[]{}),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0)
                ), new List[]{}),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(180.0, 0.0)
                ), new List[]{}),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)
                ), new List[]{List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.8, 0.8))}),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)
                ), new List[]{List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(120.8, 0.8))})
        );
    }

    @SafeVarargs
    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsVararg")
    final void eagerValidation_withVarArg_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<Position> exterior, List<Position>... lines) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Polygon.of(exterior, lines));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    static Stream<Arguments> eagerValidation_whenHolesPositionsAsList() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.exterior.ring.empty", List.of(), List.of()),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0)
                ), List.of()),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(180.0, 0.0)
                ), List.of()),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)
                ), List.of(List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.8, 0.8)))),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", List.of(
                        Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)
                ), List.of(List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(120.8, 0.8))))
        );
    }

    @ParameterizedTest
    @MethodSource("eagerValidation_whenHolesPositionsAsList")
    final void eagerValidation_withExteriorAndHolesList_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, List<Position> exterior, List<List<Position>> holes) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Polygon.of(exterior, holes));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }

    static Stream<Arguments> eagerValidation_whenPositionsAsCoordinates() {
        return Stream.of(
                Arguments.of("coordinates", "coordinates.exterior.ring.empty", new PolygonCoordinates()),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", new PolygonCoordinates(
                        List.of(List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0)))
                )),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", new PolygonCoordinates(
                        List.of(List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(180.0, 0.0)))
                )),
                Arguments.of("coordinates", "coordinates.ring.length.invalid", new PolygonCoordinates(
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
                        List.of(List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.8, 0.8)))
                )),
                Arguments.of("coordinates", "coordinates.ring.circle.invalid", new PolygonCoordinates(
                        List.of(Position.of(100.0, 0.0), Position.of(101.0, 0.0), Position.of(101.0, 1.0), Position.of(100.0, 1.0), Position.of(100.0, 0.0)),
                        List.of(List.of(Position.of(100.8, 0.8), Position.of(100.8, 0.2), Position.of(100.2, 0.2), Position.of(100.2, 0.8), Position.of(120.8, 0.8)))
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("eagerValidation_whenPositionsAsCoordinates")
    final void eagerValidation_withCoordinates_whenValidationFails_shouldProvideExpectedError(String errorField, String errorKey, PolygonCoordinates coordinates) {

        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Polygon.of(coordinates));

        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo(errorField))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(errorKey))
                )
        );
    }


    @Test
    void serialization_withExterior_shouldCreateValidGeoJson() throws IOException {
        Polygon polygon = Polygon.of(PolygonCoordinates.of(EXTERIOR_RING));
        String jsonContent = objectMapper.writeValueAsString(polygon);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(POLYGON_WITHOUT_HOLES_JSON);
    }

    @Test
    void serialization_withExteriorAndHoles_shouldCreateValidGeoJson() throws IOException {
        Polygon polygon = Polygon.of(PolygonCoordinates.of(EXTERIOR_RING, INTERIOR_RING));
        String jsonContent = objectMapper.writeValueAsString(polygon);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(POLYGON_WITH_HOLES_JSON);
    }

    @Test
    void serialization_withExteriorAndHoles_andWithBaseTypeGeometry_shouldCreateValidGeoJson() throws IOException {
        Geometry geometry = Polygon.of(PolygonCoordinates.of(EXTERIOR_RING, INTERIOR_RING));
        String jsonContent = objectMapper.writeValueAsString(geometry);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(POLYGON_WITH_HOLES_JSON);
    }

    @Test
    void serialization_withExteriorAndHoles_andWithBaseTypeGeoJson_shouldCreateValidGeoJson() throws IOException {
        GeoJson geoJson = Polygon.of(PolygonCoordinates.of(EXTERIOR_RING, INTERIOR_RING));
        String jsonContent = objectMapper.writeValueAsString(geoJson);
        assertThat(jsonContent).isEqualToIgnoringWhitespace(POLYGON_WITH_HOLES_JSON);
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

            Polygon polygon = Polygon.of(exteriorRing, hole);
            assertThat(polygon).hasToString("Polygon{type='Polygon', coordinates=[[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]]}");
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

            Polygon location1Variant1 = Polygon.of(exteriorRing, hole);
            Polygon location1Variant2 = Polygon.of(exteriorRing, hole);

            Polygon location2Variant1 = Polygon.of(hole, exteriorRing);
            Polygon location2Variant2 = Polygon.of(hole, exteriorRing);

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

            Polygon location1Variant1 = Polygon.of(exteriorRing, hole);
            Polygon location1Variant2 = Polygon.of(exteriorRing, hole);

            Polygon location2Variant1 = Polygon.of(hole, exteriorRing);
            Polygon location2Variant2 = Polygon.of(hole, exteriorRing);

            assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
            assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
        });
    }

}
