package com.barclays.oas.generator.model.ir;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String description;
    private String contentType;
    private String schemaRef;
}
