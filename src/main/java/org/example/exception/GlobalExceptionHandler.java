package org.example.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BaseResponse;
import org.example.util.Utils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Global exception handler for generate custom rest response structure.
 *
 * @see <a href="https://gist.githubusercontent.com/ehabqadah/30e17bebe0b7b00e40c6565868d0ec37/raw/20874b4a8f7861072f6b2aff7daa4b8b1c8b995f/GeneralExceptionHandler.java">sources 1</a>
 * @see <a href="https://smattme.com/blog/technology/spring-boot-exception-handling">sources 2</a>
 */
@Slf4j
@RestController
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements ErrorController {
    private static final String FIELD_ERROR_SEPARATOR = ": ";
    private static final String ERRORS_FOR_PATH = "errors {} for path {}";

    @GetMapping("/error")
    public ResponseEntity<?> handleError(HttpServletRequest request) {
        final HttpStatus httpStatus = HttpStatus.valueOf((int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        if (log.isErrorEnabled())
            log.error(ERRORS_FOR_PATH, request.getAttribute(RequestDispatcher.ERROR_MESSAGE),
                    request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI), request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));

        final BaseResponse<Object> data  = BaseResponse.builder()
                .timestamp(Instant.now())
                .errors(Set.of(
                        switch (httpStatus) {
                            case UNAUTHORIZED -> getSimpleMessage(request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString());
                            case FORBIDDEN -> "Yours user (" + request.getAttribute(RequestDispatcher.ERROR_MESSAGE) + ") not synchronized yet.";
                            default -> httpStatus.getReasonPhrase();
                        }))
                .message(Utils.getMessageForStatus(httpStatus))
                .errorType(log.isErrorEnabled() && request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) != null
                        ? request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE).toString()
                        : null)
                .build();
        return ResponseEntity.status(httpStatus).body(data);
    }

    /**
     * Override default catching {@link TypeMismatchException} exceptions and return custom response with {@link BaseResponse}.
     * Get when call controller's method with wrong parameter's types.
     *
     * @param e       the exception to handle
     * @param headers the headers to use for the response
     * @param status  the status code to use for the response
     * @param request the current request
     *
     * @return        {@code ResponseEntity} with BaseResponse
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getExceptionResponseEntity(e, status, Utils.getMessageForStatus(status), getErrors(e), request);
    }

    /**
     * Override default catching {@link MethodArgumentNotValidException} exceptions and return custom response with {@link BaseResponse}.
     * Get when using @Valid annotation with controller's methods parameter(s).
     *
     * @param e       the exception to handle
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     *
     * @return        {@code ResponseEntity} with BaseResponse
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Set<String> errors = e.getFieldErrors().stream()
                .map(field -> field.getField() + FIELD_ERROR_SEPARATOR + field.getDefaultMessage() + "; Пришло" + FIELD_ERROR_SEPARATOR + field.getRejectedValue())
                .collect(Collectors.toSet());
        return getExceptionResponseEntity(e, status, Utils.getMessageForStatus(status), errors, request);
    }

    /**
     * Catch {@link HttpMessageNotReadableException} exceptions and return custom response with {@link BaseResponse}.
     * Get when try parse wrong request.
     *
     * @param e       the exception to handle
     * @param headers the headers to use for the response
     * @param status  the status code to use for the response
     * @param request the current request
     *
     * @return        {@code ResponseEntity} with BaseResponse
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getExceptionResponseEntity(e, status, Utils.getMessageForStatus(status), getErrors(e), request);
    }

    /**
     * Catch {@link CustomAppException} exceptions and return custom response with {@link BaseResponse}.
     * Get when validating entity with rules.
     *
     * @param e       custom exception data
     * @param request contains request uri
     *
     * @return        {@code ResponseEntity} with BaseResponse
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final Set<String> errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + FIELD_ERROR_SEPARATOR + violation.getMessage() + "; Пришло" + FIELD_ERROR_SEPARATOR + violation.getInvalidValue())
                .collect(Collectors.toSet());
        return getExceptionResponseEntity(e, status, Utils.getMessageForStatus(status), errors, request);
    }

    /**
     * Catch {@link CustomAppException} exceptions and return custom response with {@link BaseResponse}.
     *
     * @param e       custom exception data
     * @param request contains request uri
     *
     * @return        {@code ResponseEntity} with BaseResponse
     */
    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<Object> handleCustomErrors(CustomAppException e, WebRequest request) {
        final HttpStatus status = e.getHttpStatus();
        final Set<String> errors = (e.getErrors() == null || e.getErrors().isEmpty()) ? Utils.getExceptionMessageChain(e, e.getMessage()) : e.getErrors();
        return getExceptionResponseEntity(e, status, e.getMessage(), errors, request);
    }

    /**
     * Catch all other exceptions and return custom response with {@link BaseResponse}
     *
     * @param e       exception data
     * @param request contains request uri
     *
     * @return        {@code ResponseEntity} with BaseResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllErrors(Exception e, WebRequest request) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return getExceptionResponseEntity(e, status, Utils.getMessageForStatus(status), getErrors(e), request);
    }

    private Set<String> getErrors(final Exception e) {
        final String error = e.getLocalizedMessage();
        Set<String> errors = null;
        if (log.isErrorEnabled() && error.contains(FIELD_ERROR_SEPARATOR)) {
            errors = error.split(FIELD_ERROR_SEPARATOR).length > 1
                    ? Utils.getExceptionMessageChain(e)
                    : Collections.singleton(error.substring(0, error.indexOf(FIELD_ERROR_SEPARATOR)));
        }
        return errors;
    }

    private ResponseEntity<Object> getExceptionResponseEntity(final Exception e, final HttpStatus status, final String message, final Set<String> errors, WebRequest request) {
        final String path = request.getDescription(false);
        if (log.isErrorEnabled())
            log.error(ERRORS_FOR_PATH, e.getMessage(), path, e);
        final BaseResponse<?> response = BaseResponse.builder()
                .timestamp(Instant.now())
                .message(message)
                .errors(errors)
                .errorType(log.isErrorEnabled() ? e.getClass().getSimpleName() : null)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    private String getSimpleMessage(String fullMessage) {
        return fullMessage.substring(fullMessage.indexOf(FIELD_ERROR_SEPARATOR) + 2);
    }
}
