package com.github.nramc.geojson.examples.resources;

import com.github.nramc.geojson.domain.GeoJson;
import com.github.nramc.geojson.validator.ValidationResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GeoJsonValidationResource {

    @GetMapping(value = "/rest/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationResult> validateGeoJson(@RequestBody GeoJson geoJson) {
        return ResponseEntity.ok(geoJson.validate());
    }

}
