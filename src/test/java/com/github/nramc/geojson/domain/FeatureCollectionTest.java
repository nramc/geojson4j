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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE_COLLECTION;
import static org.assertj.core.api.Assertions.assertThat;

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
    void validate_shouldReturnErrorsForInvalidType() {
        FeatureCollection collection = new FeatureCollection("InvalidType", null);
        assertThat(collection.validate().hasErrors()).isTrue();
        assertThat(collection.validate().getErrors())
                .anyMatch(error -> error.getKey().equals("type.invalid"));
    }

    @Test
    void validate_shouldReturnNoErrorsForValidCollection() {
        Feature feature = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        FeatureCollection collection = FeatureCollection.of(feature);
        assertThat(collection.validate().hasErrors()).isFalse();
    }

    @Test
    void constructor_shouldAcceptNullFeatures() {
        FeatureCollection collection = new FeatureCollection(FEATURE_COLLECTION, null);
        assertThat(collection.getFeatures()).isEmpty();
    }

    @Test
    void defaultConstructor_shouldCreateValidInstance() {
        FeatureCollection collection = new FeatureCollection();
        assertThat(collection.getType()).isEqualTo(FEATURE_COLLECTION);
        assertThat(collection.getFeatures()).isEmpty();
    }

    @Test
    void factoryMethod_shouldCreateValidCollection() {
        Feature feature1 = Feature.of("a9fa1f6a-b1b2-4030-b02f-b3d451558656", Point.of(45.0, 45.0), Map.of("name", "Park"));
        Feature feature2 = Feature.of("1e4b1fa0-b3f6-48cd-a9d5-78ffa9e5ac42", Point.of(95.0, 10.0), Map.of("name", "Temple"));

        FeatureCollection collection = FeatureCollection.of(feature1, feature2);

        assertThat(collection.getType()).isEqualTo(FEATURE_COLLECTION);
        assertThat(collection.getFeatures()).hasSize(2);
        assertThat(collection.isValid()).isTrue();
    }

}
