package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE;
import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FeatureTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization() throws IOException {
        String json = """
                {
                  "id": "ID_001",
                  "type": "Feature",
                  "geometry": {"type": "Polygon", "coordinates": [[[11.539417624693925, 48.17613313877797], [11.538077298468238, 48.168150074081154], [11.561556116500725, 48.1685970352552], [11.558505718881861, 48.1759482169781], [11.539417624693925, 48.17613313877797]]]},
                  "properties": {"name": "Olympic Park", "size": 85}
                }""";
        Feature object = objectMapper.readValue(json, Feature.class);
        assertThat(object).isNotNull()
                .satisfies(feature -> assertThat(feature.getType()).isEqualTo(FEATURE))
                .satisfies(feature -> assertThat(feature.isValid()).isTrue()).satisfies(feature -> assertThat(feature.isValid()).isTrue())
                .satisfies(feature -> assertThat(feature.getId()).isEqualTo("ID_001"))
                .satisfies(feature -> assertThat(feature.getGeometry()).extracting(GeoJson::getType).isEqualTo(POLYGON))
                .satisfies(feature -> assertThat(feature.getProperties()).contains(entry("name", "Olympic Park")))
                .satisfies(feature -> assertThat(feature.getProperties()).contains(entry("size", 85)));
    }

    @Test
    void deserialization_withoutId() throws IOException {
        String json = """
                {
                  "type": "Feature",
                  "geometry": {"type": "Polygon", "coordinates": [[[11.539417624693925, 48.17613313877797], [11.538077298468238, 48.168150074081154], [11.561556116500725, 48.1685970352552], [11.558505718881861, 48.1759482169781], [11.539417624693925, 48.17613313877797]]]},
                  "properties": {"name": "Olympic Park", "size": 85}
                }""";
        Feature object = objectMapper.readValue(json, Feature.class);
        assertThat(object).isNotNull()
                .satisfies(feature -> assertThat(feature.getType()).isEqualTo(FEATURE))
                .satisfies(feature -> assertThat(feature.getId()).isNullOrEmpty())
                .satisfies(feature -> assertThat(feature.isValid()).isTrue())
                .satisfies(feature -> assertThat(feature.getGeometry()).extracting(GeoJson::getType).isEqualTo(POLYGON))
                .satisfies(feature -> assertThat(feature.getProperties()).contains(entry("name", "Olympic Park")))
                .satisfies(feature -> assertThat(feature.getProperties()).contains(entry("size", 85)));
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        assertDoesNotThrow(() -> {
            Feature feature = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
            assertThat(feature).isNotNull()
                    .hasToString("Feature{type='Feature', id='a9fa1f6a-b1b2-4030-b02f-b3d451558656', geometry=Point{type='Point', coordinates=[45.0, 45.0]}, properties={name=Park}}");
        });
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        assertDoesNotThrow(() -> {
            Feature location1Variant1 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
            Feature location1Variant2 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));

            Feature location2Variant1 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));
            Feature location2Variant2 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));

            assertThat(location1Variant1).isEqualTo(location1Variant2);
            assertThat(location2Variant1).isEqualTo(location2Variant2);

            assertThat(location1Variant1).isNotEqualTo(location2Variant1);
            assertThat(location1Variant2).isNotEqualTo(location2Variant2);
        });
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        assertDoesNotThrow(() -> {
            Feature location1Variant1 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
            Feature location1Variant2 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));

            Feature location2Variant1 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));
            Feature location2Variant2 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));

            assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
            assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);

        });
    }

}