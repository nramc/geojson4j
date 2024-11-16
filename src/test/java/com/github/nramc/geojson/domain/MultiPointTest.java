package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.geojson.constant.GeoJsonType;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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

}