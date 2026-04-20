package com.barclays.oas.generator.parser;

import com.barclays.oas.generator.exception.OasParseException;
import com.barclays.oas.generator.model.ir.ApiSpec;

public interface OasParserService {
    ApiSpec parse(String oasFilePath) throws OasParseException;

    ApiSpec parseFromString(String oasContent) throws OasParseException;
}
