package com.barclays.oas.generator.model.ir;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldModel {
    private String name;
    private String javaType;
    private boolean required;
    private boolean nullable;
    private Object defaultValue;
    private List<ValidationRule> validations;
    private String description;
    private boolean deprecated;
}
