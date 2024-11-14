package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', delimiter = ';', textBlock = """
            # GeoJson,                                                          Expected Error
            { "type": "Point", "coordinates": [60.8, 20.5, 54.7, 11.2] };        coordinates.length.invalid
            { "type": "Point", "coordinates": [] };                              coordinates.length.invalid
            { "type": "Point", "coordinates": [60.8, 20.5, 54.7, 10.4, 15.7] };  coordinates.length.invalid
            { "type": "Point", "coordinates": [60.8] };                          coordinates.length.invalid
            { "type": "Point", "coordinates": [-190.0, 20.5] };                  coordinates.longitude.invalid
            { "type": "Point", "coordinates": [-190.0, 20.5, 54.7] };            coordinates.longitude.invalid
            { "type": "Point", "coordinates": [60.8, -190.0] };                  coordinates.latitude.invalid
            { "type": "Point", "coordinates": [60.8, -190.0, 54.7] };            coordinates.latitude.invalid
            { "type": "Point", "coordinates": [60.8, -120.0] };                  coordinates.latitude.invalid
            { "type": "Point", "coordinates": [60.8, -120.0, 54.7] };            coordinates.latitude.invalid
            { "type": "Point", "coordinates": [54.7, 95.0] };                    coordinates.latitude.invalid
            { "type": "Point", "coordinates": [54.7, 95.0, 54.7] };              coordinates.latitude.invalid
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

}