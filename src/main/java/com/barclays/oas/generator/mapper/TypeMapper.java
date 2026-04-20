package com.barclays.oas.generator.mapper;

import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TypeMapper {

    public String mapOasTypeToJavaType(Schema<?> schema) {
        if (schema == null) {
            return "Object";
        }

        String type = schema.getType();
        String format = schema.getFormat();

        if ("string".equals(type)) {
            if ("date-time".equals(format)) {
                return "LocalDateTime";
            }
            if ("date".equals(format)) {
                return "LocalDate";
            }
            if ("uuid".equals(format)) {
                return "UUID";
            }
            if ("binary".equals(format) || "byte".equals(format)) {
                return "byte[]";
            }
            return "String";
        }

        if ("integer".equals(type)) {
            if ("int64".equals(format)) {
                return "Long";
            }
            return "Integer";
        }

        if ("number".equals(type)) {
            if ("float".equals(format)) {
                return "Float";
            }
            if ("double".equals(format)) {
                return "Double";
            }
            return "Double";
        }

        if ("boolean".equals(type)) {
            return "Boolean";
        }

        if ("array".equals(type)) {
            return "List";
        }

        if ("object".equals(type)) {
            return "Map";
        }

        log.debug("Unknown OAS type mapping for type={} format={}", type, format);
        return "Object";
    }
}
