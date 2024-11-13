package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.nramc.geojson.constant.GeoJsonType;

import java.io.Serializable;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = com.github.nramc.geojson.constant.GeoJsonType.POINT),
        @JsonSubTypes.Type(value = MultiPoint.class, name = GeoJsonType.MULTI_POINT),
        @JsonSubTypes.Type(value = LineString.class, name = GeoJsonType.LINE_STRING),
        @JsonSubTypes.Type(value = MultiLineString.class, name = GeoJsonType.MULTI_LINE_STRING),
        @JsonSubTypes.Type(value = Polygon.class, name = GeoJsonType.POLYGON),
        @JsonSubTypes.Type(value = MultiPolygon.class, name = GeoJsonType.MULTI_POLYGON),
        @JsonSubTypes.Type(value = GeometryCollection.class, name = GeoJsonType.GEOMETRY_COLLECTION),
        @JsonSubTypes.Type(value = Feature.class, name = GeoJsonType.FEATURE),
        @JsonSubTypes.Type(value = FeatureCollection.class, name = GeoJsonType.FEATURE_COLLECTION)
})
public abstract sealed class GeoJson implements Serializable permits Feature, FeatureCollection, Geometry {

    public abstract String getType();

}
