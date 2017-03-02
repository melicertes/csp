package com.sastix.csp.exceptions;

public class InvalidDataTypeException extends Exception {

    private static final String message = "EX_DSL_1 “Invalid data type”";

    public InvalidDataTypeException() {
        super();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
