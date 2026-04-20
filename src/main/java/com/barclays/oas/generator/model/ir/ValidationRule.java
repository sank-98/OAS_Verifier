package com.barclays.oas.generator.model.ir;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRule {
    private ValidationType type;
    private Object value;
    private Object secondaryValue;

    public static ValidationRule notNull() {
        return ValidationRule.builder().type(ValidationType.NOT_NULL).build();
    }

    public static ValidationRule size(int min, int max) {
        return ValidationRule.builder().type(ValidationType.SIZE).value(min).secondaryValue(max).build();
    }

    public static ValidationRule min(long minValue) {
        return ValidationRule.builder().type(ValidationType.MIN).value(minValue).build();
    }

    public static ValidationRule max(long maxValue) {
        return ValidationRule.builder().type(ValidationType.MAX).value(maxValue).build();
    }

    public static ValidationRule pattern(String regex) {
        return ValidationRule.builder().type(ValidationType.PATTERN).value(regex).build();
    }

    public static ValidationRule email() {
        return ValidationRule.builder().type(ValidationType.EMAIL).build();
    }

    public enum ValidationType {
        NOT_NULL, SIZE, MIN, MAX, PATTERN, EMAIL, UNIQUE, FORMAT, CUSTOM
    }
}
