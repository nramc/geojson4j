package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE;
import static com.github.nramc.geojson.constant.GeoJsonType.FEATURE_COLLECTION;
import static com.github.nramc.geojson.constant.GeoJsonType.GEOMETRY_COLLECTION;
import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POLYGON;
import static com.github.nramc.geojson.constant.GeoJsonType.POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;
import static org.assertj.core.api.Assertions.assertThat;

class GeoJsonTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialization_withPoint() throws IOException {
        String json = """
                { "type": "Point", "coordinates": [100.0, 0.0] }""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(POINT))
                .isInstanceOf(Point.class);
    }

    @Test
    void deserialization_withMultiPoint() throws IOException {
        String json = """
                { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ] }""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_POINT))
                .isInstanceOf(MultiPoint.class);
    }

    @Test
    void deserialization_withLineString() throws IOException {
        String json = """
                { "type": "LineString", "coordinates": [ [101.0, 0.0], [102.0, 1.0] ] }""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(LINE_STRING))
                .isInstanceOf(LineString.class);
    }

    @Test
    void deserialization_withMultiLineString() throws IOException {
        String json = """
                { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ] ] }""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_LINE_STRING))
                .isInstanceOf(MultiLineString.class);
    }

    @Test
    void deserialization_withPolygonAndWithoutHoles() throws IOException {
        String json = """
                {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]}""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(POLYGON))
                .isInstanceOf(Polygon.class);
    }

    @Test
    void deserialization_withPolygonAndWithHoles() throws IOException {
        String json = """
                { "type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]] }""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(POLYGON))
                .isInstanceOf(Polygon.class);
    }

    @Test
    void deserialization_withMultiPolygon() throws IOException {
        String json = """
                {"type": "MultiPolygon", "coordinates": [[[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(MULTI_POLYGON))
                .isInstanceOf(MultiPolygon.class);
    }

    @Test
    void deserialization_withGeometryCollection() throws IOException {
        String json = """
                {
                  "type": "GeometryCollection",
                  "geometries": [
                    {"type": "Point", "coordinates": [100.0, 0.0]},
                    {"type": "LineString", "coordinates": [[101.0, 0.0], [102.0, 1.0]]},
                    {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]},
                    {"type": "Polygon", "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]]},
                    {"type": "MultiPoint", "coordinates": [[100.0, 0.0], [101.0, 1.0]]},
                    {"type": "MultiLineString", "coordinates": [[[100.0, 0.0], [101.0, 1.0]], [[102.0, 2.0], [103.0, 3.0]]]},
                    {"type": "MultiPolygon", "coordinates": [[[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}
                  ]
                }""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GEOMETRY_COLLECTION))
                .isInstanceOf(GeometryCollection.class);
    }

    @Test
    void deserialization_withFeature() throws IOException {
        String json = """
                {"id": "ID_001", "type": "Feature", "geometry": {"type": "Polygon", "coordinates": [[[11.539417624693925, 48.17613313877797], [11.538077298468238, 48.168150074081154], [11.561556116500725, 48.1685970352552], [11.558505718881861, 48.1759482169781], [11.539417624693925, 48.17613313877797]]]}, "properties": {"name": "Olympic Park", "size": 85}}""";
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(FEATURE))
                .isInstanceOf(Feature.class);
    }

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
        GeoJson geoJson = objectMapper.readValue(json, GeoJson.class);
        assertThat(geoJson).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(FEATURE_COLLECTION))
                .isInstanceOf(FeatureCollection.class);
    }

}