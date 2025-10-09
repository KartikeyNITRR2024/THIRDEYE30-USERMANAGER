package com.thirdeye3.usermanager.exceptions.handler;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.exceptions.ForbiddenException;
import com.thirdeye3.usermanager.exceptions.MessageBrokerException;
import com.thirdeye3.usermanager.exceptions.PropertyFetchException;
import com.thirdeye3.usermanager.exceptions.RoleNotFoundException;
import com.thirdeye3.usermanager.exceptions.UserNotFoundException;
import com.thirdeye3.usermanager.exceptions.ThresholdNotFoundException;
import com.thirdeye3.usermanager.exceptions.TelegramChatIdNotFoundException;
import com.thirdeye3.usermanager.exceptions.ThresholdGroupNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PropertyFetchException.class)
    public ResponseEntity<Response<Void>> handlePropertyFetch(PropertyFetchException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(MessageBrokerException.class)
    public ResponseEntity<Response<Void>> handlePropertyFetch(MessageBrokerException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response<Void>> handleUserNotFound(UserNotFoundException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Response<Void>> handleRoleNotFound(RoleNotFoundException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    
    
    @ExceptionHandler(ThresholdGroupNotFoundException.class)
    public ResponseEntity<Response<Void>> handleThresholdGroupNotFound(ThresholdGroupNotFoundException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                          .getFieldErrors()
                          .stream()
                          .map(FieldError::getDefaultMessage)
                          .collect(Collectors.joining(", "));

        Response<Void> response = new Response<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                errors,
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    

    @ExceptionHandler(ThresholdNotFoundException.class)
    public ResponseEntity<Response<Void>> handleThresholdNotFound(ThresholdNotFoundException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TelegramChatIdNotFoundException.class)
    public ResponseEntity<Response<Void>> handleTelegramChatIdNotFound(TelegramChatIdNotFoundException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response<Void>> handleForbiddenException(ForbiddenException ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGeneric(Exception ex) {
        Response<Void> response = new Response<>(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error occurred: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
