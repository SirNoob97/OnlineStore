package com.sirnoob.authservice.validator;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@Component
public class ConstraintValidator {

  private final Validator validator;

  public ConstraintValidator(Validator validator){
    this.validator = validator;
  }

  public <T> Mono<T> validate(T t) {
    Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
    String errors = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
    if(!errors.isEmpty())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors);

    return Mono.just(t);
  }
}
