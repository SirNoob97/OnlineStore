package com.sirnoob.authservice.exception;

public class CustomException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CustomException(String message) {
    super(message);
  }
}
