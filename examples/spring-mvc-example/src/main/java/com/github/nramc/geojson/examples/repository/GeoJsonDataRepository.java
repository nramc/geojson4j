package com.github.nramc.geojson.examples.repository;

import com.github.nramc.geojson.examples.repository.entity.GeoJsonDataEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface GeoJsonDataRepository extends ListCrudRepository<GeoJsonDataEntity, Long> {
}
