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
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialisation_withValidLongitudeAndLatitude_shouldProvideValidJson() throws IOException {
        String jsonContent = objectMapper.writeValueAsString(Point.of(Position.of(60.8, 20.5)));
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "Point", "coordinates": [60.8, 20.5] }""");
    }

    @Test
    void serialisation_withValidLongitudeAndLatitudeAndAltitude_shouldProvideValidJson() throws IOException {
        String jsonContent = objectMapper.writeValueAsString(Point.of(Position.of(60.8, 20.5, 43.2)));
        assertThat(jsonContent).isEqualToIgnoringWhitespace("""
                { "type": "Point", "coordinates": [60.8, 20.5, 43.2] }""");
    }

    @Test
    void deserialization_withValidLongitudeAndLatitude_shouldCreateValidObject() throws IOException {
        String jsonString = """
                { "type": "Point", "coordinates": [60.8, 20.5] }""";
        assertThat(objectMapper.readValue(jsonString, Point.class))
                .isNotNull()
                .satisfies(point -> assertThat(point.getType()).isEqualTo("Point"))
                .satisfies(point -> assertThat(point.getCoordinates()).isNotNull()
                        .extracting(Position::getCoordinates).isEqualTo(new double[]{60.8, 20.5}))
                .satisfies(point -> assertThat(point.isValid()).isTrue())
                .satisfies(point -> assertThat(point.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isFalse())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors()).isEmpty()));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitude_withGeoJsonBaseType_shouldCreateValidObject() throws IOException {
        String jsonString = """
                { "type": "Point", "coordinates": [60.8, 20.5] }""";
        GeoJson geoJson = objectMapper.readValue(jsonString, GeoJson.class);
        assertThat(geoJson).isNotNull().satisfies(point -> assertThat(point.getType()).isEqualTo("Point"));
        assertThat((Point) geoJson).satisfies(point -> assertThat(point.getCoordinates()).isNotNull()
                        .extracting(Position::getCoordinates).isEqualTo(new double[]{60.8, 20.5}))
                .satisfies(point -> assertThat(point.isValid()).isTrue())
                .satisfies(point -> assertThat(point.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isFalse())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors()).isEmpty()));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitudeAndAltitude_shouldCreateValidObject() throws IOException {
        String jsonString = """
                { "type": "Point", "coordinates": [60.8, 20.5, 54.7] }""";
        assertThat(objectMapper.readValue(jsonString, Point.class))
                .satisfies(point -> assertThat(point.getType()).isEqualTo("Point"))
                .satisfies(point -> assertThat(point.getCoordinates()).isNotNull()
                        .extracting(Position::getCoordinates).isEqualTo(new double[]{60.8, 20.5, 54.7}))
                .satisfies(point -> assertThat(point.isValid()).isTrue())
                .satisfies(point -> assertThat(point.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isFalse())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors()).isEmpty()));
    }

    @Test
    void deserialization_withValidLongitudeAndLatitudeAndAltitude_withGeometryBaseType_shouldCreateValidObject() throws IOException {
        String jsonString = """
                { "type": "Point", "coordinates": [60.8, 20.5, 54.7] }""";
        Geometry geometry = objectMapper.readValue(jsonString, Geometry.class);
        assertThat(geometry).satisfies(point -> assertThat(point.getType()).isEqualTo("Point"));
        assertThat((Point) geometry)
                .satisfies(point -> assertThat(point.getCoordinates()).isNotNull()
                        .extracting(Position::getCoordinates).isEqualTo(new double[]{60.8, 20.5, 54.7}))
                .satisfies(point -> assertThat(point.isValid()).isTrue())
                .satisfies(point -> assertThat(point.validate())
                        .satisfies(validationResult -> assertThat(validationResult.hasErrors()).isFalse())
                        .satisfies(validationResult -> assertThat(validationResult.getErrors()).isEmpty()));
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # GeoJson,                                                          Expected Error
            { "type": "Point", "coordinates": [60.8, 20.5, 54.7, 11.2] };       coordinates.length.invalid
            { "type": "Point", "coordinates": [] };                             coordinates.length.invalid
            { "type": "Point", "coordinates": [60.8, 20.5, 54.7, 10.4, 15.7] }; coordinates.length.invalid
            { "type": "Point", "coordinates": [60.8] };                         coordinates.length.invalid
            { "type": "Point", "coordinates": [-190.0, 20.5] };                 coordinates.longitude.invalid
            { "type": "Point", "coordinates": [-190.0, 20.5, 54.7] };           coordinates.longitude.invalid
            { "type": "Point", "coordinates": [60.8, -190.0] };                 coordinates.latitude.invalid
            { "type": "Point", "coordinates": [60.8, -190.0, 54.7] };           coordinates.latitude.invalid
            { "type": "Point", "coordinates": [60.8, -120.0] };                 coordinates.latitude.invalid
            { "type": "Point", "coordinates": [60.8, -120.0, 54.7] };           coordinates.latitude.invalid
            { "type": "Point", "coordinates": [54.7, 95.0] };                   coordinates.latitude.invalid
            { "type": "Point", "coordinates": [54.7, 95.0, 54.7] };             coordinates.latitude.invalid
            """)
    void deserialization_withInvalidCoordinates_shouldCreateObject_withInvalidStatus(String geoJson, String expectedErrorKey) throws JsonProcessingException {
        assertThat(objectMapper.readValue(geoJson, Point.class))
                .satisfies(point -> assertThat(point.getType()).isEqualTo("Point"))
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

    @Test
    void eagerValidation_whenStaticMethodUsed_withPosition() {
        Position position = new Position();
        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Point.of(position));
        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo("coordinates"))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo("coordinates.longitude.invalid"))
                )
        );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # Longitude  Latitude     Expected Key
            123.09872;   96.2345;     coordinates.latitude.invalid
            123.09872;   -96.2345;    coordinates.latitude.invalid
            250.09872;   90.2345;     coordinates.longitude.invalid
            -340.09872;  90.2345;     coordinates.longitude.invalid
            """)
    void eagerValidation_whenStaticMethodUsed_withLongitudeAndLatitude(double longitude, double latitude, String expectedErrorKey) {
        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Point.of(longitude, latitude));
        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo("coordinates"))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                )
        );
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # Longitude  Latitude   altitude    Expected Key
            123.09872;   96.2345;   11000;      coordinates.latitude.invalid
            123.09872;   -96.2345;  -11000;     coordinates.latitude.invalid
            250.09872;   90.2345;   9000;       coordinates.longitude.invalid
            -340.09872;  90.2345;   1000;       coordinates.longitude.invalid
            """)
    void eagerValidation_whenStaticMethodUsed_withLongitudeAndLatitudeAndAltitude(double longitude, double latitude, double altitude, String expectedErrorKey) {
        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Point.of(longitude, latitude, altitude));
        assertThat(validationException).isNotNull().satisfies(validationResult ->
                assertThat(validationResult.getErrors()).isNotEmpty().anySatisfy(error ->
                        assertThat(error).satisfies(e -> assertThat(e.getField()).isEqualTo("coordinates"))
                                .satisfies(e -> assertThat(e.getKey()).isEqualTo(expectedErrorKey))
                )
        );
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        Point point = Point.of(108.1134, 45.24567);
        assertThat(point).hasToString("Point{type='Point', coordinates=[108.1134, 45.24567]}");
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        Point location1Variant1 = Point.of(108.1134, 45.24567);
        Point location1Variant2 = Point.of(108.1134, 45.24567);

        Point location2Variant1 = Point.of(25.1234, -54.1234);
        Point location2Variant2 = Point.of(25.1234, -54.1234);

        assertThat(location1Variant1).isEqualTo(location1Variant2);
        assertThat(location2Variant1).isEqualTo(location2Variant2);

        assertThat(location1Variant1).isNotEqualTo(location2Variant1);
        assertThat(location1Variant2).isNotEqualTo(location2Variant2);
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        Point location1Variant1 = Point.of(108.1134, 45.24567);
        Point location1Variant2 = Point.of(108.1134, 45.24567);

        Point location2Variant1 = Point.of(25.1234, -54.1234);
        Point location2Variant2 = Point.of(25.1234, -54.1234);

        assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
        assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
    }

}
