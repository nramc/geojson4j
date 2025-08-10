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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE;
import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

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
    void checkFeatureProperties_shouldSatisfyAsExpected() {
        Feature feature = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        assertThat(feature).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(FEATURE))
                .satisfies(obj -> assertThat(obj.getId()).isEqualTo("a9fa1f6a-b1b2-4030-b02f-b3d451558656"))
                .satisfies(obj -> assertThat(obj.getProperties()).containsExactly(entry("name", "Park")))
                .satisfies(obj -> assertThat(obj.getProperty("name")).isEqualTo("Park"))
                .satisfies(obj -> assertThat(obj.getProperty("size")).isNull())
                .satisfies(obj -> assertThat(obj.getPropertyIfExists("name")).hasValue("Park"))
                .satisfies(obj -> assertThat(obj.getPropertyIfExists("size")).isEmpty())
                .satisfies(obj -> assertThat(obj.getGeometry()).isEqualTo(Point.of(45.0, 45.0)))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue());
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        Feature feature = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        assertThat(feature).isNotNull()
                .hasToString("Feature{type='Feature', id='a9fa1f6a-b1b2-4030-b02f-b3d451558656', geometry=Point{type='Point', coordinates=[45.0, 45.0]}, properties={name=Park}}");
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        Feature location1Variant1 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        Feature location1Variant2 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));

        Feature location2Variant1 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));
        Feature location2Variant2 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));

        assertThat(location1Variant1).isEqualTo(location1Variant2);
        assertThat(location2Variant1).isEqualTo(location2Variant2);

        assertThat(location1Variant1).isNotEqualTo(location2Variant1);
        assertThat(location1Variant2).isNotEqualTo(location2Variant2);
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        Feature location1Variant1 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        Feature location1Variant2 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));

        Feature location2Variant1 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));
        Feature location2Variant2 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));

        assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
        assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);

    }

    @Test
    void verifyJavaSerializationAndDeserialization() throws IOException, ClassNotFoundException {
        Feature originalFeature = Feature.of(
                "test-id-123",
                Point.of(45.0, 45.0),
                Map.of("name", "Test Location", "type", "Park")
        );

        // Serialize to byte array
        byte[] serialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(originalFeature);
            serialized = baos.toByteArray();
        }

        // Deserialize from byte array
        Feature deserializedFeature;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserializedFeature = (Feature) ois.readObject();
        }

        // Verify the deserialized object equals the original
        assertThat(deserializedFeature)
                .isNotNull()
                .isEqualTo(originalFeature);
        assertThat(deserializedFeature.getId()).isEqualTo(originalFeature.getId());
        assertThat(deserializedFeature.getGeometry()).isEqualTo(originalFeature.getGeometry());
        assertThat(deserializedFeature.getProperties()).isEqualTo(originalFeature.getProperties());
    }

    @Test
    void verifyJsonSerializationAndDeserialization() throws IOException {
        Feature originalFeature = Feature.of(
                "test-id-456",
                Point.of(180.0, 90.0),
                Map.of("name", "JSON Test", "category", "Monument")
        );

        // Serialize to JSON string
        String jsonString = objectMapper.writeValueAsString(originalFeature);

        // Verify JSON string matches expected format
        String expectedJson = """
                {
                  "type": "Feature",
                  "id": "test-id-456",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [180.0, 90.0]
                  },
                  "properties": {
                    "name": "JSON Test",
                    "category": "Monument"
                  }
                }""";

        // Compare JSON strings by parsing them to remove formatting differences
        assertThat(objectMapper.readTree(jsonString))
                .isEqualTo(objectMapper.readTree(expectedJson));

        // Deserialize from JSON string
        Feature deserializedFeature = objectMapper.readValue(jsonString, Feature.class);

        // Verify the deserialized object equals the original
        assertThat(deserializedFeature).isNotNull().isEqualTo(originalFeature);
        assertThat(deserializedFeature.getId()).isEqualTo(originalFeature.getId());
        assertThat(deserializedFeature.getGeometry()).isEqualTo(originalFeature.getGeometry());
        assertThat(deserializedFeature.getProperties()).isEqualTo(originalFeature.getProperties());
    }

    @Test
    void testJsonSerialization_VerifyJsonStructure() throws IOException {
        Feature feature = Feature.of(
                "test-id-789",
                Point.of(35.0, -75.0),
                Map.of("name", "Structure Test", "visited", true)
        );

        // Serialize to JSON string
        String jsonString = objectMapper.writeValueAsString(feature);

        // Verify JSON structure using JsonNode
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        assertThat(jsonNode.get("type").asText()).isEqualTo("Feature");
        assertThat(jsonNode.get("id").asText()).isEqualTo("test-id-789");

        JsonNode geometryNode = jsonNode.get("geometry");
        assertThat(geometryNode.get("type").asText()).isEqualTo("Point");
        assertThat(geometryNode.get("coordinates").get(0).asDouble()).isEqualTo(35.0);
        assertThat(geometryNode.get("coordinates").get(1).asDouble()).isEqualTo(-75.0);

        JsonNode propertiesNode = jsonNode.get("properties");
        assertThat(propertiesNode.get("name").asText()).isEqualTo("Structure Test");
        assertThat(propertiesNode.get("visited").asBoolean()).isTrue();
    }
}
