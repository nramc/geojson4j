package com.github.nramc.geojson.validator;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

public class ValidationResult {
    private final Set<String> errors;

    public ValidationResult(Set<String> errors) {
        this.errors = errors;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return CollectionUtils.isNotEmpty(errors);
    }


}
