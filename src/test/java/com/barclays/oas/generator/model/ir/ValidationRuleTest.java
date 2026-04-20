package com.barclays.oas.generator.model.ir;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValidationRuleTest {

    @Test
    void factoryMethodsShouldPopulateExpectedValidationData() {
        ValidationRule notNull = ValidationRule.notNull();
        assertEquals(ValidationRule.ValidationType.NOT_NULL, notNull.getType());
        assertNull(notNull.getValue());

        ValidationRule size = ValidationRule.size(1, 10);
        assertEquals(ValidationRule.ValidationType.SIZE, size.getType());
        assertEquals(1, size.getValue());
        assertEquals(10, size.getSecondaryValue());

        ValidationRule min = ValidationRule.min(5L);
        assertEquals(ValidationRule.ValidationType.MIN, min.getType());
        assertEquals(5L, min.getValue());

        ValidationRule max = ValidationRule.max(20L);
        assertEquals(ValidationRule.ValidationType.MAX, max.getType());
        assertEquals(20L, max.getValue());

        ValidationRule pattern = ValidationRule.pattern("^[a-z]+$");
        assertEquals(ValidationRule.ValidationType.PATTERN, pattern.getType());
        assertEquals("^[a-z]+$", pattern.getValue());

        ValidationRule email = ValidationRule.email();
        assertEquals(ValidationRule.ValidationType.EMAIL, email.getType());
    }
}
