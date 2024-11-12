package com.github.nramc.geojson.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialization_withMandatoryValuesOnly() throws IOException {
        Position position = Position.of(10.0, 15.0);
        String jsonContent = objectMapper.writeValueAsString(position);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("[10.0, 15.0]");
    }

    @Test
    void serialization_withMandatoryAndOptionalValues() throws IOException {
        Position position = Position.of(10.0, 15.0, 25.0);
        String jsonContent = objectMapper.writeValueAsString(position);
        assertThat(jsonContent).isEqualToIgnoringWhitespace("[10.0, 15.0, 25.0]");
    }

    @Test
    void deserialization_withAllValues() throws IOException {
        String jsonString = "[18.5, 23.9, 30.2]";
        Position objectContent = objectMapper.readValue(jsonString, Position.class);
        assertThat(objectContent).isNotNull()
                .satisfies(obj -> assertThat(obj.getLongitude()).isEqualTo(18.5))
                .satisfies(obj -> assertThat(obj.getLatitude()).isEqualTo(23.9))
                .satisfies(obj -> assertThat(obj.getAltitude()).isEqualTo(30.2));
    }

    @Test
    void deserialization_withMandatoryValues() throws IOException {
        String jsonString = "[24.1, 56.3]";
        Position objectContent = objectMapper.readValue(jsonString, Position.class);
        assertThat(objectContent).isNotNull()
                .satisfies(obj -> assertThat(obj.getLongitude()).isEqualTo(24.1))
                .satisfies(obj -> assertThat(obj.getLatitude()).isEqualTo(56.3))
                .satisfies(obj -> assertThat(obj.getAltitude()).isIn(Double.NaN));
    }

    @ParameterizedTest
    @ValueSource(strings = {"[24.1]", "[]", "[1, 2, 3, 4]", "[-190, 90]", "[100.0, -180]", "[190, 74]", "[80.0, 98.0]"})
    void deserialization_withInvalidValues_shouldThrowSerializationError(String jsonString) throws JsonProcessingException {
        Position position = objectMapper.readValue(jsonString, Position.class);
        assertThat(position).isNotNull().extracting(Position::isValid).isEqualTo(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"[invalid]", "0", "c", "", " "})
    void deserialization_withInvalidValues_shouldBeDeserializable_withInvalidStatus(String jsonString) {
        Assertions.assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(jsonString, Position.class));
    }

}