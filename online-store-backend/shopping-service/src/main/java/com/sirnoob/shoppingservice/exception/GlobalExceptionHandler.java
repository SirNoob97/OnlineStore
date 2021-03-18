package com.sirnoob.shoppingservice.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


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
  public ResponseEntity<ExceptionDetails> handleConstraintViolationException(DataIntegrityViolationException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                          .body(ExceptionDetails.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .title(exception.getRootCause().getLocalizedMessage())
                                                .detail(exception.getMessage())
                                                .exceptionClassName(exception.getClass().getName())
                                                .build());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
    String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
    String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

    //@formatter:off
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                          .body(ValidationExceptionDetails.builder()
                                                          .exceptionDetails(ExceptionDetails.builder()
                                                                                            .timestamp(LocalDateTime.now())
                                                                                            .status(HttpStatus.BAD_REQUEST.value())
                                                                                            .title("Field Validation Error")
                                                                                            .detail("The fields below don't meet the requirements")
                                                                                            .exceptionClassName(exception.getClass().getName()).build())
                                                          .fields(fields)
                                                          .fieldsMessage(fieldsMessage)
                                                          .build());
  //@formatter:on
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception exception, @Nullable Object body,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    return ResponseEntity.status(status).headers(headers).body(ExceptionDetails.builder()
                                                                                .timestamp(LocalDateTime.now())
                                                                                .status(status.value())
                                                                                .title(exception.getCause().getMessage())
                                                                                .detail(exception.getMessage())
                                                                                .exceptionClassName(exception.getClass().getName()).build());

  }
}
