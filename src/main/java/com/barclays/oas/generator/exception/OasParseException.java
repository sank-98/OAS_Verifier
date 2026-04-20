package com.barclays.oas.generator.exception;

public class OasParseException extends RuntimeException {
    public OasParseException(String message) {
        super(message);
    }

    public OasParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public OasParseException(Throwable cause) {
        super(cause);
    }
}
