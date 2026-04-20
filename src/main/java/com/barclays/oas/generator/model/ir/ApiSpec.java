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
public class ApiSpec {
    private String title;
    private String version;
    private String description;
    private String contactName;
    private String contactEmail;
    private String licenseName;
    private String termsOfService;
    private String baseUrl;
    private List<ApiEndpoint> endpoints;
    private List<SchemaModel> schemas;
}
