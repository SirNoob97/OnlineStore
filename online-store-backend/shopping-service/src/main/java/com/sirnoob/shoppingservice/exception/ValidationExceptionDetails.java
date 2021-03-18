package com.sirnoob.shoppingservice.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ValidationExceptionDetails {

  private ExceptionDetails exceptionDetails;
  private String fields;
  private String fieldsMessage;
}
