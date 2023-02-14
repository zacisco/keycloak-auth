package org.example.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Getter
public class CustomAppException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final Set<String> errors;

    /**
     * Generate exception with {@link HttpStatus} INTERNAL_SERVER_ERROR code.
     *
     * @param message   text message,error description
     */
    public CustomAppException(String message) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Generate exception with {@link HttpStatus} INTERNAL_SERVER_ERROR code.
     *
     * @param message  text message,error description
     * @param errors   for chain errors in response message;
     */
    public CustomAppException(String message, Set<String> errors) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message, errors);
    }

    /**
     * Generate exception with {@link HttpStatus} INTERNAL_SERVER_ERROR code.
     *
     * @param message   text message,error description
     * @param throwable for chain errors in response message and error log;
     */
    public CustomAppException(String message, Throwable throwable) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message, throwable);
    }

    /**
     * Generate exception with {@link HttpStatus} INTERNAL_SERVER_ERROR code.
     *
     * @param message   text message,error description
     * @param errors    for chain errors in response message;
     * @param throwable for chain errors in error log;
     */
    public CustomAppException(String message, Set<String> errors, Throwable throwable) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message, errors, throwable);
    }

    /**
     * Generate exception with selected {@link HttpStatus} code.
     *
     * @param httpStatus {@link HttpStatus} as code and title http response
     * @param message    text message,error description
     */
    public CustomAppException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, null, null);
    }

    /**
     * Generate exception with selected {@link HttpStatus} code.
     *
     * @param httpStatus {@link HttpStatus} as code and title http response
     * @param errors     for chain errors in response message;
     */
    public CustomAppException(HttpStatus httpStatus, Set<String> errors) {
        this(httpStatus, null, errors, null);
    }

    /**
     * Generate exception with selected {@link HttpStatus} code.
     *
     * @param httpStatus {@link HttpStatus} as code and title http response
     * @param throwable  for chain errors in error log;
     */
    public CustomAppException(HttpStatus httpStatus, Throwable throwable) {
        this(httpStatus, null, null, throwable);
    }

    /**
     * Generate exception with selected {@link HttpStatus} code.
     *
     * @param httpStatus {@link HttpStatus} as code and title http response
     * @param message    text message,error description
     * @param errors     for chain errors in response message;
     */
    public CustomAppException(HttpStatus httpStatus, String message, Set<String> errors) {
        this(httpStatus, message, errors, null);
    }

    /**
     * Generate exception with selected {@link HttpStatus} code.
     *
     * @param httpStatus {@link HttpStatus} as code and title http response
     * @param message    text message,error description
     * @param throwable  for chain errors in error log;
     */
    public CustomAppException(HttpStatus httpStatus, String message, Throwable throwable) {
        this(httpStatus, message, null, throwable);
    }

    /**
     * Generate exception with selected {@link HttpStatus} code.
     *
     * @param httpStatus {@link HttpStatus} as code and title http response
     * @param message    text message,error description
     * @param errors     for chain errors in response message;
     * @param throwable  for chain errors in error log;
     */
    public CustomAppException(HttpStatus httpStatus, String message, Set<String> errors, Throwable throwable) {
        super(message, throwable);
        this.httpStatus = httpStatus;
        this.errors = errors;
    }
}
