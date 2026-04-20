package com.barclays.oas.generator.model.ir;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiEndpoint {
    private String path;
    private HttpMethod httpMethod;
    private String operationId;
    private String summary;
    private String description;
    private List<String> tags;
    private List<ApiParameter> parameters;
    private ApiRequestBody requestBody;
    private Map<Integer, ApiResponse> responses;
    private boolean deprecated;
}
