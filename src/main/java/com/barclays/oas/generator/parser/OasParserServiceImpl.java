package com.barclays.oas.generator.parser;

import com.barclays.oas.generator.exception.OasParseException;
import com.barclays.oas.generator.mapper.TypeMapper;
import com.barclays.oas.generator.mapper.ValidationAnnotationMapper;
import com.barclays.oas.generator.model.ir.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OasParserServiceImpl implements OasParserService {

    private final TypeMapper typeMapper;
    private final ValidationAnnotationMapper validationMapper;

    public OasParserServiceImpl(TypeMapper typeMapper, ValidationAnnotationMapper validationMapper) {
        this.typeMapper = typeMapper;
        this.validationMapper = validationMapper;
    }

    @Override
    public ApiSpec parse(String oasFilePath) throws OasParseException {
        try {
            log.info("Parsing OpenAPI specification from: {}", oasFilePath);
            
            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            ParseOptions options = new ParseOptions();
            options.setResolve(true);
            options.setResolveFully(true);
            
            OpenAPI openAPI = parser.read(oasFilePath, null, options);
            
            if (openAPI == null) {
                throw new OasParseException("Failed to parse OpenAPI specification at: " + oasFilePath);
            }
            
            return mapOpenApiToSpec(openAPI);
        } catch (OasParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error parsing OpenAPI specification", e);
            throw new OasParseException("Failed to parse OpenAPI specification: " + e.getMessage(), e);
        }
    }

    @Override
    public ApiSpec parseFromString(String oasContent) throws OasParseException {
        try {
            log.info("Parsing OpenAPI specification from string content");
            
            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            ParseOptions options = new ParseOptions();
            options.setResolve(true);
            options.setResolveFully(true);
            
            OpenAPI openAPI = parser.parseURI(oasContent, null, options);
            
            if (openAPI == null) {
                throw new OasParseException("Failed to parse OpenAPI specification from content");
            }
            
            return mapOpenApiToSpec(openAPI);
        } catch (OasParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error parsing OpenAPI specification from string", e);
            throw new OasParseException("Failed to parse OpenAPI specification: " + e.getMessage(), e);
        }
    }

    private ApiSpec mapOpenApiToSpec(OpenAPI openAPI) {
        ApiSpec spec = new ApiSpec();
        
        // Map info
        if (openAPI.getInfo() != null) {
            spec.setTitle(openAPI.getInfo().getTitle());
            spec.setVersion(openAPI.getInfo().getVersion());
            spec.setDescription(openAPI.getInfo().getDescription());
            
            if (openAPI.getInfo().getContact() != null) {
                spec.setContactName(openAPI.getInfo().getContact().getName());
                spec.setContactEmail(openAPI.getInfo().getContact().getEmail());
            }
            
            if (openAPI.getInfo().getLicense() != null) {
                spec.setLicenseName(openAPI.getInfo().getLicense().getName());
            }
            
            spec.setTermsOfService(openAPI.getInfo().getTermsOfService());
        }

        // Map servers
        if (openAPI.getServers() != null && !openAPI.getServers().isEmpty()) {
            spec.setBaseUrl(openAPI.getServers().get(0).getUrl());
        }

        // Map paths to endpoints
        List<ApiEndpoint> endpoints = new ArrayList<>();
        if (openAPI.getPaths() != null) {
            for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
                endpoints.addAll(mapPathToEndpoints(pathEntry.getKey(), pathEntry.getValue()));
            }
        }
        spec.setEndpoints(endpoints);
        log.info("Parsed {} endpoints", endpoints.size());

        // Map schemas
        List<SchemaModel> schemas = new ArrayList<>();
        if (openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
            for (Map.Entry<String, Schema> schemaEntry : openAPI.getComponents().getSchemas().entrySet()) {
                schemas.add(mapSchemaToModel(schemaEntry.getKey(), schemaEntry.getValue()));
            }
        }
        spec.setSchemas(schemas);
        log.info("Parsed {} schemas", schemas.size());

        return spec;
    }

    private List<ApiEndpoint> mapPathToEndpoints(String path, PathItem pathItem) {
        List<ApiEndpoint> endpoints = new ArrayList<>();

        Map<String, Operation> operations = new HashMap<>();
        if (pathItem.getGet() != null) operations.put("GET", pathItem.getGet());
        if (pathItem.getPost() != null) operations.put("POST", pathItem.getPost());
        if (pathItem.getPut() != null) operations.put("PUT", pathItem.getPut());
        if (pathItem.getDelete() != null) operations.put("DELETE", pathItem.getDelete());
        if (pathItem.getPatch() != null) operations.put("PATCH", pathItem.getPatch());
        if (pathItem.getHead() != null) operations.put("HEAD", pathItem.getHead());
        if (pathItem.getOptions() != null) operations.put("OPTIONS", pathItem.getOptions());
        if (pathItem.getTrace() != null) operations.put("TRACE", pathItem.getTrace());

        for (Map.Entry<String, Operation> opEntry : operations.entrySet()) {
            endpoints.add(mapOperationToEndpoint(path, opEntry.getKey(), opEntry.getValue()));
        }

        return endpoints;
    }

    private ApiEndpoint mapOperationToEndpoint(String path, String method, Operation operation) {
        ApiEndpoint endpoint = new ApiEndpoint();
        
        endpoint.setPath(path);
        endpoint.setHttpMethod(HttpMethod.valueOf(method));
        endpoint.setOperationId(operation.getOperationId());
        endpoint.setSummary(operation.getSummary());
        endpoint.setDescription(operation.getDescription());
        endpoint.setTags(operation.getTags());
        endpoint.setDeprecated(operation.getDeprecated() != null && operation.getDeprecated());

        // Map parameters
        List<ApiParameter> parameters = new ArrayList<>();
        if (operation.getParameters() != null) {
            for (Parameter param : operation.getParameters()) {
                parameters.add(mapParameterToModel(param));
            }
        }
        endpoint.setParameters(parameters);

        // Map request body
        if (operation.getRequestBody() != null) {
            endpoint.setRequestBody(mapRequestBodyToModel(operation.getRequestBody()));
        }

        // Map responses
        Map<Integer, com.barclays.oas.generator.model.ir.ApiResponse> responses = new HashMap<>();
        if (operation.getResponses() != null) {
            for (Map.Entry<String, ApiResponse> respEntry : operation.getResponses().entrySet()) {
                try {
                    int statusCode = Integer.parseInt(respEntry.getKey());
                    responses.put(statusCode, mapResponseToModel(respEntry.getValue()));
                } catch (NumberFormatException e) {
                    if ("default".equals(respEntry.getKey())) {
                        responses.put(200, mapResponseToModel(respEntry.getValue()));
                    }
                }
            }
        }
        endpoint.setResponses(responses);

        return endpoint;
    }

    private ApiParameter mapParameterToModel(Parameter param) {
        ApiParameter apiParam = new ApiParameter();
        
        apiParam.setName(param.getName());
        apiParam.setRequired(param.getRequired() != null && param.getRequired());
        apiParam.setDescription(param.getDescription());
        
        if (param.getIn() != null) {
            apiParam.setLocation(ParameterLocation.valueOf(param.getIn().toUpperCase()));
        }

        if (param.getSchema() != null) {
            apiParam.setType(typeMapper.mapOasTypeToJavaType(param.getSchema()));
            List<ValidationRule> validations = validationMapper.mapSchemaToRules(param.getSchema(), apiParam.isRequired());
            apiParam.setValidations(validations);
        }

        return apiParam;
    }

    private ApiRequestBody mapRequestBodyToModel(io.swagger.v3.oas.models.RequestBody requestBody) {
        ApiRequestBody apiReqBody = new ApiRequestBody();
        
        apiReqBody.setRequired(requestBody.getRequired() != null && requestBody.getRequired());
        apiReqBody.setDescription(requestBody.getDescription());
        
        if (requestBody.getContent() != null && !requestBody.getContent().isEmpty()) {
            io.swagger.v3.oas.models.media.MediaType mediaType = requestBody.getContent().values().iterator().next();
            if (mediaType.getSchema() != null) {
                apiReqBody.setSchemaRef(getSchemaReference(mediaType.getSchema()));
            }
        }

        return apiReqBody;
    }

    private com.barclays.oas.generator.model.ir.ApiResponse mapResponseToModel(ApiResponse apiResponse) {
        com.barclays.oas.generator.model.ir.ApiResponse response = new com.barclays.oas.generator.model.ir.ApiResponse();
        
        response.setDescription(apiResponse.getDescription());
        
        if (apiResponse.getContent() != null && !apiResponse.getContent().isEmpty()) {
            Map.Entry<String, io.swagger.v3.oas.models.media.MediaType> entry = apiResponse.getContent().entrySet().iterator().next();
            response.setContentType(entry.getKey());
            
            if (entry.getValue().getSchema() != null) {
                response.setSchemaRef(getSchemaReference(entry.getValue().getSchema()));
            }
        }

        return response;
    }

    private SchemaModel mapSchemaToModel(String name, Schema<?> schema) {
        SchemaModel schemaModel = new SchemaModel();
        
        schemaModel.setName(name);
        schemaModel.setDescription(schema.getDescription());
        schemaModel.setDeprecated(schema.getDeprecated() != null && schema.getDeprecated());

        if ("object".equals(schema.getType())) {
            schemaModel.setType(SchemaType.OBJECT);
            
            List<FieldModel> fields = new ArrayList<>();
            if (schema.getProperties() != null) {
                for (Map.Entry<String, Schema> propEntry : schema.getProperties().entrySet()) {
                    fields.add(mapPropertyToField(propEntry.getKey(), propEntry.getValue(), schema.getRequired()));
                }
            }
            schemaModel.setFields(fields);
        } else if ("array".equals(schema.getType())) {
            schemaModel.setType(SchemaType.ARRAY);
        } else if (schema.getEnum() != null) {
            schemaModel.setType(SchemaType.ENUM);
            schemaModel.setEnumValues(schema.getEnum().stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            schemaModel.setType(SchemaType.PRIMITIVE);
        }

        return schemaModel;
    }

    private FieldModel mapPropertyToField(String name, Schema<?> schema, List<String> requiredFields) {
        FieldModel field = new FieldModel();
        
        field.setName(name);
        field.setJavaType(typeMapper.mapOasTypeToJavaType(schema));
        field.setRequired(requiredFields != null && requiredFields.contains(name));
        field.setNullable(schema.getNullable() != null && schema.getNullable());
        field.setDescription(schema.getDescription());
        field.setDeprecated(schema.getDeprecated() != null && schema.getDeprecated());

        List<ValidationRule> validations = validationMapper.mapSchemaToRules(schema, field.isRequired());
        field.setValidations(validations);

        return field;
    }

    private String getSchemaReference(Schema<?> schema) {
        if (schema.get$ref() != null) {
            return schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1);
        }
        return typeMapper.mapOasTypeToJavaType(schema);
    }
}
