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
public class ApiParameter {
    private String name;
    private ParameterLocation location;
    private String type;
    private boolean required;
    private List<ValidationRule> validations;
    private String description;
}
