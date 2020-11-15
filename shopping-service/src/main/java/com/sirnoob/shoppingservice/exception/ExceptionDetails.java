package com.sirnoob.shoppingservice.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExceptionDetails {

  private String title;
  private int status;
  private String detail;
  private String exceptionClassName;
  private LocalDateTime timestamp;
}
