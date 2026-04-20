package com.barclays.oas.generator.mapper;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeMapperTest {

    private final TypeMapper typeMapper = new TypeMapper();

    @Test
    void shouldMapStringFormatsToJavaTypes() {
        assertEquals("LocalDateTime", typeMapper.mapOasTypeToJavaType(new DateTimeSchema()));
        assertEquals("LocalDate", typeMapper.mapOasTypeToJavaType(new DateSchema()));

        StringSchema uuidSchema = new StringSchema();
        uuidSchema.setFormat("uuid");
        assertEquals("UUID", typeMapper.mapOasTypeToJavaType(uuidSchema));

        StringSchema emailSchema = new StringSchema();
        emailSchema.setFormat("email");
        assertEquals("String", typeMapper.mapOasTypeToJavaType(emailSchema));

        StringSchema binarySchema = new StringSchema();
        binarySchema.setFormat("binary");
        assertEquals("byte[]", typeMapper.mapOasTypeToJavaType(binarySchema));

        StringSchema byteSchema = new StringSchema();
        byteSchema.setFormat("byte");
        assertEquals("byte[]", typeMapper.mapOasTypeToJavaType(byteSchema));
    }

    @Test
    void shouldMapNumericAndBooleanTypesToJavaTypes() {
        IntegerSchema int32Schema = new IntegerSchema();
        int32Schema.setFormat("int32");
        assertEquals("Integer", typeMapper.mapOasTypeToJavaType(int32Schema));

        IntegerSchema int64Schema = new IntegerSchema();
        int64Schema.setFormat("int64");
        assertEquals("Long", typeMapper.mapOasTypeToJavaType(int64Schema));

        NumberSchema floatSchema = new NumberSchema();
        floatSchema.setFormat("float");
        assertEquals("Float", typeMapper.mapOasTypeToJavaType(floatSchema));

        NumberSchema doubleSchema = new NumberSchema();
        doubleSchema.setFormat("double");
        assertEquals("Double", typeMapper.mapOasTypeToJavaType(doubleSchema));

        assertEquals("Boolean", typeMapper.mapOasTypeToJavaType(new BooleanSchema()));
    }

    @Test
    void shouldMapCollectionAndObjectTypesToJavaTypes() {
        assertEquals("List", typeMapper.mapOasTypeToJavaType(new ArraySchema()));
        assertEquals("Map", typeMapper.mapOasTypeToJavaType(new ObjectSchema()));
    }
}
