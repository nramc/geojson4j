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

import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;


/**
 * Represents the result of a validation operation.
 * <p>
 * This class encapsulates a set of validation errors and provides methods to
 * check if any errors occurred. It is used to aggregate and represent validation
 * results in a structured way.
 * </p>
 *
 * <p>Example usage:
 * <pre>{@code
 * Set<ValidationError> errors = new HashSet<>();
 * errors.add(new ValidationError("coordinates", "coordinates cannot be empty", "coordinates.invalid.empty"));
 * ValidationResult result = new ValidationResult(errors);
 *
 * if (result.hasErrors()) {
 *     // Handle validation errors
 * }
 * }</pre></p>
 */
public class ValidationResult {
    private final Set<ValidationError> errors;

    /**
     * Constructs a new {@code ValidationResult} with the specified set of validation errors.
     *
     * @param errors A set of {@link ValidationError} objects describing the validation errors.
     *               Must not be null. If there are no errors, an empty set should be provided.
     */
    public ValidationResult(Set<ValidationError> errors) {
        this.errors = errors;
    }

    /**
     * Returns the set of validation errors associated with this result.
     *
     * @return A set of {@link ValidationError} objects. Never null, but may be empty if there
     * are no validation errors.
     */
    public Set<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Checks if there are any validation errors.
     *
     * @return {@code true} if there are one or more validation errors, otherwise {@code false}.
     */
    public boolean hasErrors() {
        return CollectionUtils.isNotEmpty(errors);
    }


}
