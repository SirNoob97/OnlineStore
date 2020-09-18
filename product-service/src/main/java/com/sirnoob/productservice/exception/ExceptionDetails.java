package com.sirnoob.productservice.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ExceptionDetails {

  protected String title;
  protected int status;
  protected String detail;
  protected String exceptionClassName;
  protected LocalDateTime timestamp;
}
