package com.github.nramc.geojson.examples.repository.entity;

import com.github.nramc.geojson.domain.GeoJson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class GeoJsonDataEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = Integer.MAX_VALUE)
    private GeoJson geoJson;

    private LocalDateTime timestamp;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public GeoJson getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(GeoJson geoJson) {
        this.geoJson = geoJson;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
