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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.nramc.geojson.constant.GeoJsonType;

import java.io.Serializable;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = GeoJsonType.POINT),
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
