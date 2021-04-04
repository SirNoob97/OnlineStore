package com.sirnoob.productservice.handler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.sirnoob.productservice.exception.ExceptionDetails;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.exception.ValidationExceptionDetails;

import org.springframework.dao.DataIntegrityViolationException;
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

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ExceptionDetails> handleResourceNotFoundException(ResourceNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                          .body(ExceptionDetails.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .title("Resource Not Found")
                                .detail(exception.getMessage())
                                .exceptionClassName(exception.getClass().getName()).build());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionDetails> handleConstraintViolationException(DataIntegrityViolationException exception){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                          .body(ExceptionDetails.builder()
                                  .title(exception.getRootCause().getLocalizedMessage())
                                  .detail(exception.getMessage())
                                  .exceptionClassName(exception.getClass().getName())
                                  .status(HttpStatus.BAD_REQUEST.value())
                                  .timestamp(LocalDateTime.now())
                                  .build());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
    var fieldErrors = exception.getBindingResult().getFieldErrors();
    var fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
    var fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(ValidationExceptionDetails.builder()
                           .timestamp(LocalDateTime.now())
                           .status(HttpStatus.BAD_REQUEST.value())
                           .title("Field Validation Error")
                           .detail("The fields below don't meet the requirements")
                           .fields(fields)
                           .fieldsMessage(fieldsMessage)
                           .exceptionClassName(exception.getClass().getName())
                           .build());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
    Exception exception, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    var exceptionDetails = ExceptionDetails.builder()
                      .timestamp(LocalDateTime.now())
                      .status(status.value())
                      .title(exception.getCause().getMessage())
                      .detail(exception.getMessage())
                      .exceptionClassName(exception.getClass().getName()).build();

    return ResponseEntity.status(status).headers(headers).body(exceptionDetails);
  }
}
