package com.sirnoob.authservice.exception;

public class WrongPasswordException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public WrongPasswordException() {
    super("Wrong Password!!");
  }
}
