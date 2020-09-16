package com.sirnoob.productservice.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sirnoob.productservice.exception.ExceptionDetails;
import com.sirnoob.productservice.exception.ResourceNotFoundDetails;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.exception.ValidationExceptionDetails;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ResourceNotFoundDetails> handleResourceNotFoundException(ResourceNotFoundException exception) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ResourceNotFoundDetails.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .title("Resource Not Found")
        .detail(exception.getMessage())
        .devMessage(exception.getClass().getName()).build());
  }
  
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
    
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
    String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
    String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
    
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(ValidationExceptionDetails.builder()
                           .timestamp(LocalDateTime.now())
                           .status(HttpStatus.BAD_REQUEST.value())
                           .title("Field Validation Error")
                           .detail("The fields below don't meet the requirements")
                           .fields(fields)
                           .fieldsMessage(fieldsMessage)
                           .devMessage(exception.getClass().getName())
                           .build());
  }
  
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
    Exception exception, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    
    ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                      .timestamp(LocalDateTime.now())
                      .status(status.value())
                      .title(exception.getCause().getMessage())
                      .detail(exception.getMessage())
                      .devMessage(exception.getClass().getName()).build();
    
    return new ResponseEntity<>(exceptionDetails, headers, status);
  }
}
