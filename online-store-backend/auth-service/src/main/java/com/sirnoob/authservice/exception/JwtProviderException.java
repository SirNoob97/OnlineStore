package com.sirnoob.authservice.exception;

public class JwtProviderException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public JwtProviderException(String message) {
    super(message);
  }
}
