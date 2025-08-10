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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import com.github.nramc.geojson.validator.ValidationError;
import com.github.nramc.geojson.validator.ValidationResult;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE_COLLECTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeatureCollectionTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization_withFeatureCollection() throws IOException {
        String json = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {"id": "ID_001", "type": "Feature", "properties": {"name": "Olympic Park", "size": "85 hectares"}, "geometry": {"type": "Point", "coordinates": [100.0, 0.0]}},
                    {"id": "ID_002", "type": "Feature", "properties": {"name": "English garden", "size": "384 hectares"}, "geometry": {"type": "LineString", "coordinates": [[101.0, 0.0], [102.0, 1.0]]}},
                    {"id": "ID_003", "type": "Feature", "properties": {"name": "Hirschgarten", "size": "40 hectares"}, "geometry": {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]}}
                  ]
                }""";
        FeatureCollection featureCollection = objectMapper.readValue(json, FeatureCollection.class);
        assertThat(featureCollection).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(FEATURE_COLLECTION))
                .satisfies(obj -> assertThat(obj.getFeatures()).hasSize(3))
                .satisfies(obj -> assertThat(obj.isValid()).isTrue())
                .isInstanceOf(FeatureCollection.class);
    }

    @Test
    void toString_shouldProvideFormatedStringWithAllArguments() {
        Feature feature = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        FeatureCollection featureCollection = FeatureCollection.of(feature);
        assertThat(featureCollection)
                .isNotNull()
                .hasToString("FeatureCollection{type='FeatureCollection', features=[Feature{type='Feature', id='a9fa1f6a-b1b2-4030-b02f-b3d451558656', geometry=Point{type='Point', coordinates=[45.0, 45.0]}, properties={name=Park}}]}");
    }

    @Test
    void toString_shouldIncludeAllFields() {
        Feature feature = Feature.of("test-id", Point.of(0.0, 0.0), Map.of("name", "Test"));
        FeatureCollection collection = FeatureCollection.of(feature);

        String toString = collection.toString();

        assertThat(toString)
                .contains("FeatureCollection")
                .contains("type='" + FEATURE_COLLECTION + "'")
                .contains("features=")
                .contains("test-id")
                .contains("Point")
                .contains("name=Test");
    }

    @Test
    void equals_shouldConsiderEqualityBasedOnData() {
        Feature featurePark = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        FeatureCollection location1Variant1 = FeatureCollection.of(featurePark);
        FeatureCollection location1Variant2 = FeatureCollection.of(featurePark);

        Feature featureTemple = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));
        FeatureCollection location2Variant1 = FeatureCollection.of(featureTemple);
        FeatureCollection location2Variant2 = FeatureCollection.of(featureTemple);

        assertThat(location1Variant1).isEqualTo(location1Variant2);
        assertThat(location2Variant1).isEqualTo(location2Variant2);

        assertThat(location1Variant1).isNotEqualTo(location2Variant1);
        assertThat(location1Variant2).isNotEqualTo(location2Variant2);
    }

    @Test
    void equals_shouldHandleAllCases() {
        Feature feature1 = Feature.of("id1", Point.of(0.0, 0.0), Map.of());
        Feature feature2 = Feature.of("id2", Point.of(1.0, 1.0), Map.of());

        FeatureCollection col1 = FeatureCollection.of(feature1);
        FeatureCollection col2 = FeatureCollection.of(feature1);
        FeatureCollection col3 = FeatureCollection.of(feature2);

        assertThat(col1)
                .isEqualTo(col2)   // same content
                .isNotEqualTo(col3) // different content
                .isNotEqualTo(null) // null
                .isNotEqualTo(new Object()); // different type
    }

    @Test
    void hashCode_shouldConsiderHashCodeBasedOnData() {
        Feature featurePark = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        FeatureCollection location1Variant1 = FeatureCollection.of(featurePark);
        FeatureCollection location1Variant2 = FeatureCollection.of(featurePark);

        Feature featureTemple = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));
        FeatureCollection location2Variant1 = FeatureCollection.of(featureTemple);
        FeatureCollection location2Variant2 = FeatureCollection.of(featureTemple);

        assertThat(location1Variant1).hasSameHashCodeAs(location1Variant2);
        assertThat(location2Variant1).hasSameHashCodeAs(location2Variant2);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Feature feature = Feature.of("id1", Point.of(0.0, 0.0), Map.of());
        FeatureCollection col1 = FeatureCollection.of(feature);
        FeatureCollection col2 = FeatureCollection.of(feature);

        assertThat(col1).hasSameHashCodeAs(col2);
    }

    @Test
    void defaultConstructor_shouldCreateEmptyCollection() {
        FeatureCollection collection = new FeatureCollection();

        assertThat(collection.getType()).isEqualTo(FEATURE_COLLECTION);
        assertThat(collection.getFeatures()).isEmpty();
    }

    @Test
    void constructorWithNullFeatures_shouldCreateEmptyCollection() {
        FeatureCollection collection = new FeatureCollection(FEATURE_COLLECTION, null);

        assertThat(collection.getType()).isEqualTo(FEATURE_COLLECTION);
        assertThat(collection.getFeatures()).isEmpty();
    }

    @Test
    void validation_shouldFailForInvalidType() {
        FeatureCollection collection = new FeatureCollection("InvalidType", List.of());

        ValidationResult result = collection.validate();

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors())
                .extracting(ValidationError::getField)
                .contains("type");
    }

    @Test
    void validation_shouldValidateAllFeatures() {
        Feature validFeature = Feature.of("valid-id", Point.of(0.0, 0.0), Map.of());
        Feature invalidFeature = new Feature("Feature", "invalid-id", null, Map.of()); // null geometry

        FeatureCollection collection = new FeatureCollection(FEATURE_COLLECTION,
                List.of(validFeature, invalidFeature));

        ValidationResult result = collection.validate();

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors())
                .extracting(ValidationError::getField)
                .contains("geometry");
    }

    @Test
    void factoryMethod_shouldThrowExceptionForInvalidFeatures() {
        Feature invalidFeature = new Feature("Feature", "invalid-id", null, Map.of());
        List<Feature> features = List.of(invalidFeature);

        assertThatThrownBy(() -> FeatureCollection.of(features))
                .isInstanceOf(GeoJsonValidationException.class)
                .hasMessageContaining("geometry");
    }

    @Test
    void verifyJsonSerialization() throws IOException {
        Feature feature = Feature.of("test-id", Point.of(10.0, 20.0),
                Map.of("name", "Test Location"));
        FeatureCollection collection = FeatureCollection.of(feature);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(collection);

        // Verify JSON structure
        String expectedJson = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "id": "test-id",
                      "geometry": {
                        "type": "Point",
                        "coordinates": [10.0, 20.0]
                      },
                      "properties": {
                        "name": "Test Location"
                      }
                    }
                  ]
                }""";

        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree(expectedJson));

        // Verify deserialization
        FeatureCollection deserialized = objectMapper.readValue(json, FeatureCollection.class);
        assertThat(deserialized)
                .isNotNull()
                .isEqualTo(collection);
    }

    @Test
    void verifyJavaSerialization() throws IOException, ClassNotFoundException {
        Feature feature = Feature.of("test-id", Point.of(10.0, 20.0),
                Map.of("name", "Test Location"));
        FeatureCollection originalCollection = FeatureCollection.of(feature);

        // Serialize to byte array
        byte[] serialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(originalCollection);
            serialized = baos.toByteArray();
        }

        // Deserialize from byte array
        FeatureCollection deserializedCollection;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserializedCollection = (FeatureCollection) ois.readObject();
        }

        // Verify the deserialized object
        assertThat(deserializedCollection)
                .isNotNull()
                .isEqualTo(originalCollection);
        assertThat(deserializedCollection.getFeatures())
                .hasSameSizeAs(originalCollection.getFeatures())
                .containsExactlyElementsOf(originalCollection.getFeatures());
    }

}
