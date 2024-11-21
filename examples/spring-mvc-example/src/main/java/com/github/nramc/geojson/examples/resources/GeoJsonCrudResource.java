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
package com.github.nramc.geojson.examples.resources;

import com.github.nramc.geojson.domain.GeoJson;
import com.github.nramc.geojson.examples.repository.GeoJsonDataRepository;
import com.github.nramc.geojson.examples.repository.entity.GeoJsonDataEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class GeoJsonCrudResource {
    private final GeoJsonDataRepository geoJsonDataRepository;

    public GeoJsonCrudResource(GeoJsonDataRepository geoJsonDataRepository) {
        this.geoJsonDataRepository = geoJsonDataRepository;
    }

    @GetMapping(value = "/rest/geojson", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GeoJson> getAllGeoJsons() {
        return geoJsonDataRepository.findAll().stream().map(GeoJsonDataEntity::getGeoJson).toList();
    }

    @PostMapping(value = "/rest/geojson", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody GeoJson geoJson) {
        GeoJsonDataEntity entity = new GeoJsonDataEntity();
        entity.setGeoJson(geoJson);
        entity.setTimestamp(LocalDateTime.now());
        geoJsonDataRepository.save(entity);
    }
}
