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
