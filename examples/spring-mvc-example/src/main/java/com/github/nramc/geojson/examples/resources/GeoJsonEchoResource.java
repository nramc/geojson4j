package com.github.nramc.geojson.examples.resources;

import com.github.nramc.geojson.domain.GeoJson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeoJsonEchoResource {

    @GetMapping(value = "/rest/echo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeoJson> echo(@RequestBody final GeoJson geoJson) {
        return ResponseEntity.ok(geoJson);
    }

}
