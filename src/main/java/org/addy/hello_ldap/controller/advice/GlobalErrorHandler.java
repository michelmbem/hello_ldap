package org.addy.hello_ldap.controller.advice;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {

    private static final String LOG_MSG_TEMPLATE =
            "An exception of type {} was raised while requesting {} {}";

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Void> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        reportError(e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(NameNotFoundException.class)
    public ResponseEntity<Void> handleNameNotFoundException(NameNotFoundException e) {
        reportError(e);

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidNameException.class)
    public ResponseEntity<Void> handleInvalidNameException(InvalidNameException e) {
        reportError(e);

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        reportError(e);

        return ResponseEntity.badRequest().body(getErrorMap(e));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        reportError(e);

        return ResponseEntity.badRequest().body(Collections.singletonMap("*",
                "Malformed URL or request body. Check the logs for details"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        reportError(e);

        return ResponseEntity.internalServerError()
                .body("Something went wrong. Check the logs for details");
    }

    @NonNull
    private static Map<String, Object> getErrorMap(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();

        List<String> globalErrors = allErrors.stream()
                .filter(error -> !(error instanceof FieldError))
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        Map<String, Object> errors = allErrors.stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(FieldError::getField, error ->
                        Objects.requireNonNullElse(error.getDefaultMessage(), "")));

        if (!globalErrors.isEmpty())
            errors.put("*", globalErrors);

        return errors;
    }

    private void reportError(Exception e) {
        String method = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest()
                .getMethod();

        URL targetURL = null;

        try {
            targetURL = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .build()
                    .toUri()
                    .toURL();
        } catch (MalformedURLException e1) {
            log.error("Could not retrieve current request URL", e1);
        }

        log.error(LOG_MSG_TEMPLATE, e.getClass().getSimpleName(), method, targetURL, e);
    }
}
