package com.sirnoob.productservice.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ConstraintViolationExceptionDetails extends ExceptionDetails{

  private String constraintName;
}
