package com.sirnoob.authservice.exception;

public class RefreshTokenNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RefreshTokenNotFoundException(String message) {
    super(message);
  }
}
