package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
                .isInstanceOf(FeatureCollection.class);
    }

}