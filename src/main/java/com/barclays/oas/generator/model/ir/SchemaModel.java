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
public class SchemaModel {
    private String name;
    private SchemaType type;
    private List<FieldModel> fields;
    private List<String> enumValues;
    private String description;
    private boolean deprecated;
}
