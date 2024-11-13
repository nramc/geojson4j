package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.nramc.geojson.validator.Validatable;

import static com.github.nramc.geojson.constant.GeoJsonType.GEOMETRY_COLLECTION;
import static com.github.nramc.geojson.constant.GeoJsonType.LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_LINE_STRING;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.MULTI_POLYGON;
import static com.github.nramc.geojson.constant.GeoJsonType.POINT;
import static com.github.nramc.geojson.constant.GeoJsonType.POLYGON;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = POINT),
        @JsonSubTypes.Type(value = MultiPoint.class, name = MULTI_POINT),
        @JsonSubTypes.Type(value = LineString.class, name = LINE_STRING),
        @JsonSubTypes.Type(value = MultiLineString.class, name = MULTI_LINE_STRING),
        @JsonSubTypes.Type(value = Polygon.class, name = POLYGON),
        @JsonSubTypes.Type(value = MultiPolygon.class, name = MULTI_POLYGON),
        @JsonSubTypes.Type(value = GeometryCollection.class, name = GEOMETRY_COLLECTION)
})
public abstract sealed class Geometry extends GeoJson implements Validatable permits
        Point, MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon, GeometryCollection {

}
