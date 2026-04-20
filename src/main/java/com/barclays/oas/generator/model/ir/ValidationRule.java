package com.barclays.oas.generator.model.ir;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ValidationRule {
    private ValidationType type;
    private Map<String, Object> parameters;

    public ValidationRule() {
        this.parameters = Collections.emptyMap();
    }

    public ValidationRule(ValidationType type, Map<String, Object> parameters) {
        this.type = type;
        this.parameters = parameters == null ? Collections.emptyMap() : new HashMap<>(parameters);
    }

    public ValidationType getType() {
        return type;
    }

    public void setType(ValidationType type) {
        this.type = type;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters == null ? Collections.emptyMap() : new HashMap<>(parameters);
    }

    public static ValidationRule create(ValidationType type) {
        return new ValidationRule(type, Collections.emptyMap());
    }

    public static ValidationRule create(ValidationType type, Map<String, Object> parameters) {
        return new ValidationRule(type, parameters);
    }

    public static ValidationRule of(ValidationType type) {
        return create(type);
    }

    public static ValidationRule of(ValidationType type, Map<String, Object> parameters) {
        return create(type, parameters);
    }

    public static ValidationRule of(ValidationType type, String key, Object value) {
        Map<String, Object> params = new HashMap<>();
        params.put(key, value);
        return create(type, params);
    }
}
