package org.zerobs.polla.exception;


import io.micronaut.http.HttpStatus;

public class CustomException extends Exception {
    public static final String MESSAGE_TITLE_PROPERTY_ID = "error.unexpected.title";
    public static final String MESSAGE_TEXT_PROPERTY_ID = "error.unexpected.text";
    public static final int INTERNAL_CODE = 4000;
    public static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}