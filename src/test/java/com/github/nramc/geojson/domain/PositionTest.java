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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.geojson.validator.GeoJsonValidationException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(jsonString, Position.class));
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', textBlock = """
            # longitude,    latitude,    altitude
            181,            180,         11000
            180,            181,         10000
            181,            181,         -11000
            """)
    void eagerValidation_whenValuesInvalid_shouldThrowValidationError(double longitude, double latitude, double altitude) {
        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Position.of(longitude, latitude, altitude));
        assertThat(validationException).isNotNull()
                .extracting(GeoJsonValidationException::getErrors).asInstanceOf(InstanceOfAssertFactories.SET)
                .isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', textBlock = """
            # longitude,    latitude
            181,            180
            180,            181
            181,            181
            """)
    void eagerValidationWithMandatoryValues_whenValuesInvalid_shouldThrowValidationError(double longitude, double latitude) {
        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Position.of(longitude, latitude));
        assertThat(validationException).isNotNull()
                .extracting(GeoJsonValidationException::getErrors).asInstanceOf(InstanceOfAssertFactories.SET)
                .isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "181,180,11000",
            "180,181,10000",
            "181,181,-11000",
            "181,180",
            "180,181",
            "181,181"
    })
    void eagerValidation_asArray_whenValuesInvalid_shouldThrowValidationError(String value) {
        double[] coordinates = Arrays.stream(value.split(",")).mapToDouble(Double::parseDouble).toArray();
        GeoJsonValidationException validationException = assertThrows(GeoJsonValidationException.class, () -> Position.of(coordinates));
        assertThat(validationException).isNotNull()
                .extracting(GeoJsonValidationException::getErrors).asInstanceOf(InstanceOfAssertFactories.SET)
                .isNotEmpty();
    }

}
