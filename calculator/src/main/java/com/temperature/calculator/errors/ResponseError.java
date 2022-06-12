package com.temperature.calculator.errors;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ResponseError {

    private HttpStatus status;
    private String message;
    private List<String> errors;


    public ResponseError() {
        super();
    }

    public ResponseError(final HttpStatus status, final String message, final List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ResponseError(final HttpStatus status, final String message, final String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }



}
