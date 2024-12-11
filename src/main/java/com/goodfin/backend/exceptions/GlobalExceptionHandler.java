package com.goodfin.backend.exceptions;

import java.util.*;

import com.goodfin.backend.utils.ErrorMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
    private ErrorMapper errorMapper;

    public GlobalExceptionHandler(
            ErrorMapper errorMapper
    ) {
        this.errorMapper = errorMapper;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity handleException(Throwable e) {
        if (e instanceof ClientAbortException) {
            return null; //socket is closed, cannot return any response
        } else {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleException(RuntimeException e) {
        return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Map<String, List<String>>> body = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            try {
                fieldName = ((FieldError) error).getField();

            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }

            String message = error.getDefaultMessage();

            if (errors.get(fieldName) != null) {
                List<String> temp = errors.get(fieldName);
                temp.add(message);

                errors.put(fieldName, temp);
            } else {
                List<String> temp = new ArrayList<>();
                temp.add(message);

                errors.put(fieldName, temp);
            }
        });

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, String> body = new HashMap<>();

        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();

        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));

        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", errors);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(ConstraintViolationException.class)
//    ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
//        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
//        StringBuilder strBuilder = new StringBuilder();
//
//        constraintViolations.forEach((constraintViolation) -> {
//            String fieldName = String.format("%s", constraintViolation.getPropertyPath());
//            String message = constraintViolation.getMessage();
//            strBuilder.append(String.format("%s: %s\n", fieldName, message));
//        });
//
//        return new ResponseEntity<>(errorMapper.createErrorMap(strBuilder.substring(0, strBuilder.length()-1)), HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(Exception exception) {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");
        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired");
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
        }

        return errorDetail;
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
