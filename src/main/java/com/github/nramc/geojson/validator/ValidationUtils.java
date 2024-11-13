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
package com.github.nramc.geojson.validator;

public final class ValidationUtils {

    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates the given object and throws a GeoJsonValidationException if validation errors are found.
     *
     * @param validatable The object to validate, which must implement the Validatable interface.
     * @param <T>         The type of the validatable object, which extends Validatable.
     * @return The validated object if no errors are found.
     * @throws GeoJsonValidationException if there are validation errors.
     */
    public static <T extends Validatable> T validateAndThrowErrorIfInvalid(T validatable) {
        ValidationResult validationResult = validatable.validate();
        if (validationResult.hasErrors()) {
            throw new GeoJsonValidationException("GeoJson Invalid", validationResult.getErrors());
        }
        return validatable;
    }
}
