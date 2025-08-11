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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An interface for objects that can be validated. It provides methods to check
 * if the object is valid and to retrieve validation results.
 *
 * <p>Implementing classes must define the {@code validate()} method, which performs
 * the validation logic and returns a {@code ValidationResult} object containing
 * any errors found.</p>
 *
 * @see ValidationResult
 * @see ValidationError
 */
public interface Validatable {

    /**
     * Checks if the GeoJson is valid. GeoJson is considered valid if there are no validation errors present.
     *
     * @return {@code true} if the object is valid (i.e., there are no validation errors), otherwise {@code false}.
     */
    @JsonIgnore
    default boolean isValid() {
        return !validate().hasErrors();
    }

    /**
     * Checks if the object has any validation errors.
     *
     * @return {@code true} if the object has validation errors, otherwise {@code false}.
     */
    @JsonIgnore
    default boolean hasErrors() {
        return validate().hasErrors();
    }

    /**
     * Validates the GeoJson and returns the results of the validation.
     *
     * @return A {@code ValidationResult} result of validation process.
     * @see ValidationResult
     * @see ValidationError
     */
    ValidationResult validate();
}
